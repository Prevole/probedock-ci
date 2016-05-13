node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_DATA_PATH = PROBEDOCK_DATA_PATH

    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"

    env.PROBEDOCK_DATE = readFile 'date'

    git 'https://github.com/Prevole/probedock-ci'
    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    stage 'Start docker'
    sh 'pipeline/scripts/docker.sh'

    stage 'Start PostgresSQL and Redis'
    sh 'pipeline/scripts/postgres.sh'
    sh 'pipeline/scripts/redis.sh'

    stage 'Backup the PostgresSQL database'
    sh 'pipeline/scripts/postgres-backup.sh'



//    stage 'Checkout Probe Dock'
//    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock']], submoduleCfg: [], userRemoteConfigs: [[url: 'git@github.com:probedock/probedock.git']]]

}