

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
     * Make sure PostgreSQL and Redis are up and running
     */
    stage 'Start PostgresSQL, Redis and Nginx'
    sh 'pipelines/scripts/nginx.sh'
    sh 'pipelines/scripts/postgres.sh'
    sh 'pipelines/scripts/redis.sh'

//    /**
//     * We want to create the admin user
//     */
//    stage 'Backup'
//    build job: 'Backup', parameters: [
//        [$class: 'StringParameterValue', name: 'PROBEDOCK_ENV', value: env.PROBEDOCK_ENV],
//    ]

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-server-docker-image.sh'

    // TODO: Stop the applications
    // TODO: Stop the jobs

    stage 'Compile assets'
    sh 'pipelines/scripts/build-assets.sh'

    // TODO: Start the jobs
    // TODO: Start the apps
}