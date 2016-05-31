//noinspection GroovyAssignabilityCheck
node {
    load('../workspace@script/pipelines/src/Repos.groovy').cloneRepos()

    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('pipelines/src/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Passwords = load 'pipelines/src/Passwords.groovy'

    /**
     * Start the Nginx
     */
    stage 'Start the reverse proxy (Nginx)'
    sh 'pipelines/scripts/nginx.sh'

    /**
     * Start the PostgreSQL database server
     */
    stage 'Start PostgresSQL'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/postgres.sh'
    }

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-docker-images.sh'

    /**
     * Create the database
     */
    stage 'Setup the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/setup-database.sh'
    }

    /**
     * We want to create the admin user
     */
    stage 'Create the admin user'
    build job: 'CreateAdmin', parameters: [
        [$class: 'StringParameterValue', name: 'PROBEDOCK_ENV', value: env.PROBEDOCK_ENV],
        [$class: 'StringParameterValue', name: 'PROBEDOCK_ADMIN_USERNAME', value: PROBEDOCK_ADMIN_USERNAME],
        [$class: 'PasswordParameterValue', name: 'PROBEDOCK_ADMIN_PASSWORD', value: PROBEDOCK_ADMIN_PASSWORD],
        [$class: 'StringParameterValue', name: 'PROBEDOCK_ADMIN_EMAIL', value: PROBEDOCK_ADMIN_EMAIL]
    ]

    stage 'Deploy Probe Dock'
    build job: 'Deploy', parameters: [
        [$class: 'StringParameterValue', name: 'PROBEDOCK_ENV', value: env.PROBEDOCK_ENV]
    ]
}