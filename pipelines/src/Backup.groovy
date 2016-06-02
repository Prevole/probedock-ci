

node {
    load('../workspace@script/pipelines/lib/utils/Repos.groovy').cloneRepos()

    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('pipelines/lib/utils/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Passwords = load 'pipelines/src/Passwords.groovy'

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
     * Make a backup of the PostgreSQL database
     */
    stage 'Backup the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/postgres-backup.sh'
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