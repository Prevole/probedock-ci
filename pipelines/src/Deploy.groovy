

node {
    // Clone the pipelines repos and the probe dock server repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'images/probedock-base/probedock'], [$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    load('pipelines/src/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

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
    sh 'pipelines/scripts/probedock-docker-images.sh'

    // TODO: Stop the applications
    // TODO: Stop the jobs

    stage 'Compile assets'
    sh 'pipelines/scripts/build-assets.sh'

    stage 'Start job containers'
    sh 'pipelines/scripts/probedock-job-start.sh'

    stage 'Start app containers'
    sh 'pipelines/scripts/probedock-app-start.sh'
}