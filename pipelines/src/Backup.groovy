//noinspection GroovyAssignabilityCheck
node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV

    // Clone the pipelines repos and the probe dock server repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]

    def Passwords = load 'pipelines/src/Passwords.groovy'
    def DisableJob = load 'pipelines/src/DisableJob.groovy'

    println DisableJob.disableJob

    stage 'Disable job'
    DisableJob.disableJob('Backup')

//    /**
//     * Backup the PostgreSQL database
//     */
//    stage 'Backup the PostgresSQL database'
//    withCredentials([
//            [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME]
//    ]) {
//        sh 'pipeline/scripts/postgres-backup.sh'
//    }
}