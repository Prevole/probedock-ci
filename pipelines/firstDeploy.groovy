import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import hudson.plugins.sshslaves.*
import java.util.Collections

node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_DATA_PATH = PROBEDOCK_DATA_PATH

    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"

    env.PROBEDOCK_DATE = readFile 'date'

    git 'https://github.com/Prevole/probedock-ci'
    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    stage 'Definition of few passwords'
    input message: 'Set the password for the PostgreSQL root user', parameters: [[$class: 'StringParameterDefinition', defaultValue: '', description: '', name: 'POSTGRESQL_ROOT_PASSWORD']]

    store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

    // Keep these lines of code to replace the global storage of password by dedicated storage by environment
    // domain = new Domain(PROBEDOCK_ENV, 'The credentials for the probe dock ' + PROBEDOCK_ENV + ' environment.', Collections.<DomainSpecification>emptyList())
    // store.addDomain(domain)

    domain = Domain.global()

    store.addCredentials(domain, createPassword("postgresqlroot", "The password for the PostgreSQL root user.", env.POSTGRESQL_ROOT_PASSWORD))

    // Make sure the following variables will not be serialized for the next step which will fail due to store that is not serializable
    store = null
    domain = null

    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-docker-image.sh'

    stage 'Start PostgresSQL'
    sh 'pipelines/scripts/postgres.sh'

    stage 'Create the database'
    sh 'pipelines/scripts/probedock-create-database.sh'
}

/**
 * Store a password in the domain corresponding of the Probe Dock environment
 *
 * @param name The name of the password
 * @param description The description of the password
 * @param password The password to cipher and store
 */
def StringCredentialsImpl createPassword(name, description, password) {
    return new StringCredentialsImpl(CredentialsScope.GLOBAL, name, description, Secret.fromString(password))
}