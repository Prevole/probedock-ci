node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_DATA_PATH = PROBEDOCK_DATA_PATH

    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"

    env.PROBEDOCK_DATE = readFile 'date'

    git 'https://github.com/Prevole/probedock-ci'
    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-docker-image.sh'

    stage 'Start PostgresSQL'
    sh 'pipelines/scripts/postgres.sh'

    stage 'Create the database'
    sh 'pipelines/scripts/probedock-create-database.sh'

}