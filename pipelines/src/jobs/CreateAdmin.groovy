//noinspection GroovyAssignabilityCheck
//node {
//    load('../workspace@script/pipelines/src/utils/Repos.groovy').cloneCi()

def executeJob() {
//    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('ci/pipelines/src/utils/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    env.PROBEDOCK_ADMIN_USERNAME = PROBEDOCK_ADMIN_USERNAME
    env.PROBEDOCK_ADMIN_PASSWORD = PROBEDOCK_ADMIN_PASSWORD
    env.PROBEDOCK_ADMIN_EMAIL = PROBEDOCK_ADMIN_EMAIL

    // Ask the user for admin data if not already gathered
    if (!env.PROBEDOCK_ADMIN_USERNAME) {
        def params = input(
            message: 'Choose the Probe Dock version you want to deploy',
            parameters: [[
                $class      : 'hudson.model.StringParameterDefinition',
                defaultValue: 'admin',
                description : 'The Probe Dock administrator user name.',
                name        : 'PROBEDOCK_ADMIN_USERNAME'
            ], [
                $class      : 'hudson.model.StringParameterDefinition',
                defaultValue: '',
                description : 'The Probe Dock administrator password.',
                name        : 'PROBEDOCK_ADMIN_PASSWORD'
            ], [
                $class      : 'hudson.model.StringParameterDefinition',
                defaultValue: 'admin',
                description : 'The Probe Dock administrator email.',
                name        : 'PROBEDOCK_ADMIN_EMAIL'
            ]]
        )

        env.PROBEDOCK_ADMIN_USERNAME = params.PROBEDOCK_ADMIN_USERNAME
        env.PROBEDOCK_ADMIN_PASSWORD = params.PROBEDOCK_ADMIN_PASSWORD
        env.PROBEDOCK_ADMIN_EMAIL    = params.PROBEDOCK_ADMIN_EMAIL
    }

    def Passwords = load 'ci/pipelines/src/utils/Passwords.groovy'

    /**
     * We want to create the admin user
     */
    stage 'Create admin user'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/create-admin.sh'
    }
}