

node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_DATA_PATH = PROBEDOCK_DATA_PATH

    // Clone the pipelines repos and the probe dock server repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock'], [$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    /**
     * Prepare the date for backup file names
     */
    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"
    env.PROBEDOCK_DATE = readFile 'date'

    /**
     * Make sure docker is up and running
     */
    stage 'Start docker'
    sh 'pipeline/scripts/docker.sh'

    /**
     * Make sure PostgreSQL and Redis are up and running
     */
    stage 'Start PostgresSQL and Redis'
    sh 'pipeline/scripts/postgres.sh'
    sh 'pipeline/scripts/redis.sh'

    /**
     * Backup the PostgreSQL database
     */
    stage 'Backup the PostgresSQL database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
    ]) {
        sh 'pipeline/scripts/postgres-backup.sh'
    }

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-docker-image.sh'
}