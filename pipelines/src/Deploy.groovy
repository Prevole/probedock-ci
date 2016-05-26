

node {
    // Clone the pipelines repos and the probe dock server repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'images/probedock-base/probedock'], [$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('pipelines/src/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Passwords = load 'pipelines/src/Passwords.groovy'

    /**
     * Make sure PostgreSQL and Redis are up and running
     */
    stage 'Start PostgresSQL, Redis and Nginx'
    sh 'pipelines/scripts/nginx.sh'
    sh 'pipelines/scripts/postgres.sh'
    sh 'pipelines/scripts/redis.sh'

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker images'
    sh 'pipelines/scripts/probedock-docker-images.sh'

    /**
     * Stop the app and job containers
     */
    stage 'Stop app containers'
    sh 'pipelines/scripts/probedock-app-stop.sh'

    stage 'Stop job containers'
    sh 'pipelines/scripts/probedock-job-stop.sh'

    /**
     * Probe Dock must be done during the assets are built and copied to rp container
     */
    stage 'Compile assets'
    //sh 'pipelines/scripts/build-assets.sh'

    /**
     * Make a backup of the PostgreSQL database
     */
    stage 'Backup the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/postgres-backup.sh'
    }

    /**
     * Proceed to the migration, if any, of the Probe Dock database
     */
    stage 'Database migration'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/probedock-migration.sh'
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
        sh 'pipelines/scripts/probedock-job-start.sh'
    }

    stage 'Start app containers'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SECRET_KEY_NAME, variable: Passwords.DOCKER_SECRET_KEY_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_JWT_SECRET_NAME, variable: Passwords.DOCKER_JWT_SECRET_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_USER_NAME, variable: Passwords.DOCKER_SMTP_USER_NAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_SMTP_PASSWORD_NAME, variable: Passwords.DOCKER_SMTP_PASSWORD_NAME]
    ]) {
        sh 'pipelines/scripts/probedock-app-start.sh'
    }
}