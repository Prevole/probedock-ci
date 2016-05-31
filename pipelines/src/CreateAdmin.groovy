//noinspection GroovyAssignabilityCheck
node {
    load('../workspace@script/pipelines/src/Repos.groovy').cloneCi()

    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('pipelines/src/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    env.PROBEDOCK_ADMIN_USERNAME = PROBEDOCK_ADMIN_USERNAME
    env.PROBEDOCK_ADMIN_PASSWORD = PROBEDOCK_ADMIN_PASSWORD
    env.PROBEDOCK_ADMIN_EMAIL = PROBEDOCK_ADMIN_EMAIL

    def Passwords = load 'pipelines/src/Passwords.groovy'

    /**
     * We want to create the admin user
     */
    stage 'Create admin user'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/create-admin.sh'
    }
}