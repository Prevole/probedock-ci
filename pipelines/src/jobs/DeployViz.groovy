package jobs

def executeJob() {
    load('ci/pipelines/src/utils/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Passwords = load 'ci/pipelines/src/utils/Passwords.groovy'

    /**
     * Make sure all required services are up and running
     */
    stage 'Start Elastic Search'
    sh 'ci/pipelines/scripts/elastic.sh'

//    /**
//     * Build the Probe Dock main image
//     */
//    stage 'BuildViz docker images'
//    sh 'ci/pipelines/scripts/viz-docker-images.sh'
//
//    /**
//     * Stop the viz containers
//     */
//    stage 'Stop viz containers'
//    sh 'ci/pipelines/scripts/viz-app-stop.sh'
//
//    stage 'Start viz containers'
//    sh 'ci/pipelines/scripts/viz-app-start.sh'
}

return this