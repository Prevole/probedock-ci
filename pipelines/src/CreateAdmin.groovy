//noinspection GroovyAssignabilityCheck
node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_ADMIN_USERNAME = PROBEDOCK_ADMIN_USERNAME
    env.PROBEDOCK_ADMIN_PASSWORD = PROBEDOCK_ADMIN_PASSWORD
    env.PROBEDOCK_ADMIN_EMAIL = PROBEDOCK_ADMIN_EMAIL

    // Clone the pipelines repos and the probe dock server repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]

    def passwords = load 'pipelines/src/Passwords.groovy'

    /**
     * We want to create the admin user
     */
    stage 'Create admin user'
    withCredentials([
        [$class: 'StringBinding', credentialsId: passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'pipelines/scripts/create-admin.sh'
    }
}