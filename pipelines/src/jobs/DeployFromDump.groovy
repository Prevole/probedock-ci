package jobs

class DupmFileNameFilter implements FilenameFilter {
    public boolean accept(File file, String name) {
        println(file.getPath() + '/' + name)
        def currentFile = new File(file.getPath() + '/' + name);
        return currentFile.isFile() && currentFile.endsWith('sql') && !currentFile.isHidden()
    }
}

def executeJob() {
    def dumps = new File('/dumps')

    def dumpFiles = dumps.list(new DupmFileNameFilter())

    println(dumpFiles)

    // Ask the user for the Probe Dock version
    stage 'Choose a dump file'
    env.DUMP_FILE = input(
        message: 'Choose the dump file to load in the database',
        parameters: [[
             $class: 'hudson.model.ChoiceParameterDefinition',
             choices: dumpFiles,
             description: 'The dump file will be loaded into the database',
             name: 'DUMP_FILE'
         ]]
    )

    load('ci/pipelines/src/utils/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Passwords = load 'ci/pipelines/src/utils/Passwords.groovy'

    /**
     * Make sure PostgreSQL and Redis are up and running
     */
    stage 'Start PostgresSQL, Redis and Nginx'
    sh 'ci/pipelines/scripts/nginx.sh'
    sh 'ci/pipelines/scripts/postgres.sh'
    sh 'ci/pipelines/scripts/redis.sh'

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker images'
    sh 'ci/pipelines/scripts/probedock-docker-images.sh'

    /**
     * Stop the app and job containers
     */
    stage 'Stop app containers'
    sh 'ci/pipelines/scripts/probedock-app-stop.sh'

    stage 'Stop job containers'
    sh 'ci/pipelines/scripts/probedock-job-stop.sh'

    /**
     * Probe Dock must be done during the assets are built and copied to rp container
     */
    stage 'Compile assets'
    // TODO: Uncomment line
    //sh 'ci/pipelines/scripts/build-assets.sh'

    /**
     * Make a backup of the PostgreSQL database
     */
    stage 'Backup the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/postgres-backup.sh'
    }

    stage 'Drop and create the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/drop-and-create-database.sh'
    }

    stage 'Restore the database dump'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/load-database-dump.sh'
    }

    /**
     * Proceed to the migration, if any, of the Probe Dock database
     */
    stage 'Database migration'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/probedock-migration.sh'
    }

    /**
     * Start the app and job containers
     */
    stage 'Start job containers'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SECRET_KEY_NAME, variable: Passwords.DOCKER_SECRET_KEY_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_JWT_SECRET_NAME, variable: Passwords.DOCKER_JWT_SECRET_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_USER_NAME, variable: Passwords.DOCKER_SMTP_USER_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_PASSWORD_NAME, variable: Passwords.DOCKER_SMTP_PASSWORD_NAME]
    ]) {
        sh 'ci/pipelines/scripts/probedock-job-start.sh'
    }

    stage 'Start app containers'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SECRET_KEY_NAME, variable: Passwords.DOCKER_SECRET_KEY_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_JWT_SECRET_NAME, variable: Passwords.DOCKER_JWT_SECRET_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_USER_NAME, variable: Passwords.DOCKER_SMTP_USER_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_PASSWORD_NAME, variable: Passwords.DOCKER_SMTP_PASSWORD_NAME]
    ]) {
        sh 'ci/pipelines/scripts/probedock-app-start.sh'
    }
}

return this