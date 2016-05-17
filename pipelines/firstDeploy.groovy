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

/**
 * Workaround join method to avoid rejection exception of unclassified method java.util.ArrayList join
 */
def join(List lst) {
    def sb = new StringBuilder()
    for (String s : lst) {
        sb.append(s)
    }
    return sb.toString()
}

/**
 * Generate a random string base on an alphabet and the number wanted
 */
def strGenerator(String alphabet, int n) {
    def rnd = new Random()
    def sb = new StringBuilder()

    for (int i = 0; i < n; i++) {
        sb.append(alphabet[rnd.nextInt(alphabet.length())])
    }

    return sb.toString()
}


//noinspection GroovyAssignabilityCheck
node {
    env.PROBEDOCK_ENV = PROBEDOCK_ENV
    env.PROBEDOCK_DATA_PATH = PROBEDOCK_DATA_PATH

    /**
     * Define the password names
     */
    def POSTGRESSQL_PASSWORD_NAME = env.PROBEDOCK_ENV + '-PostgreSQLRoot'

    /**
     * Define the password names in Docker Compose env vars
     */
    def DOCKER_POSTGRESQL_PASSWORD = 'POSTGRES_PASSWORD'

    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"

    env.PROBEDOCK_DATE = readFile 'date'

    git 'https://github.com/Prevole/probedock-ci'
    checkout poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace'], [$class: 'RelativeTargetDirectory', relativeTargetDir: 'probedock']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/probedock/probedock.git']]]

    /**
     * This step will ask the Probe Dock deploy for several passwords that will be used to setup the database and such things.
     *
     * All the passwords will be stored through the Credentials plugin in a secure way.
     */
    stage 'Setup Probe Dock passwords'

    // Definitions
    def passwordAlphabet = join(('A'..'Z')+('a'..'z')+('0'..'9'))
    def keysAlphabet = join(('A'..'Z')+('a'..'z')+('0'..'9'))
    def passwordLength = 32
    def keysLength = 128

    println POSTGRESSQL_PASSWORD_NAME

    // Retrieve the store
    def passwordDefinitions = [
        [name: POSTGRESSQL_PASSWORD_NAME, description: 'The root password for PostgreSQL', default: strGenerator(passwordAlphabet, passwordLength)],
        [name: env.PROBEDOCK_ENV + '-ProbeDockPostgreSQL', description: 'The password for Probe Dock PostgreSQL database.', default: strGenerator(passwordAlphabet, passwordLength)],
        [name: env.PROBEDOCK_ENV + '-SecretKeyBase', description: 'The secret key base', default: strGenerator(keysAlphabet, keysLength)],
        [name: env.PROBEDOCK_ENV + '-JWTSecret', description: 'The JWT secret', default: strGenerator(keysAlphabet, keysLength)],
        [name: env.PROBEDOCK_ENV + '-ProbeDockSmtpUser', description: 'The SMTP user used to send emails from Probe Dock', default: ''],
        [name: env.PROBEDOCK_ENV + '-ProbeDockSmtpPassword', description: 'The SMTP password', default: ''],
        [name: env.PROBEDOCK_ENV + '-ProbeDockAdminPassword', description: 'The Probe Dock admin password', default: strGenerator(passwordAlphabet, passwordLength)]
    ]

    def passwordParameters = []

    // WORKAROUND: Seems the pipeline plugin is buggy with .each, ... See: https://issues.jenkins-ci.org/browse/JENKINS-26481
    for (int i = 0; i < passwordDefinitions.size(); i++) {
        passwordParameters.add([
            $class: 'StringParameterDefinition',
            defaultValue: passwordDefinitions[i].default,
            description: passwordDefinitions[i].description,
            name: passwordDefinitions[i].name
        ])
    }

    // Ask the user for initial passwords
    def passwords = input(
        message: '<p>Define passwords.</p><p><strong style="color: red; font-size: 2em;">Attention: You MUST store the credentials information in a secure way.</strong></p>',
        parameters: passwordParameters
    )

    /**
     * The storage of the password must be done after the passwords input retrieval to avoid serialization issue. In fact,
     * the SystemCredentialsProvider$StoreImpl is not serializable
     */
    def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

    // Keep these lines of code to replace the global storage of password by dedicated storage by environment
    // domain = new Domain(PROBEDOCK_ENV, 'The credentials for the probe dock ' + PROBEDOCK_ENV + ' environment.', Collections.<DomainSpecification>emptyList())
    // store.addDomain(domain)

    // Replace this line by the two above once the Groovy sandboxing will allow to use SystemCredentialsProvider$StoreImpl.addDomain
    def domain = Domain.global()

    // Store each passwords
    for (int i = 0; i < passwordDefinitions.size(); i++) {
        println passwordDefinitions[i]
        def result = store.addCredentials(
            domain,
            new StringCredentialsImpl(CredentialsScope.GLOBAL, passwordDefinitions[i].name, passwordDefinitions[i].description, Secret.fromString(passwords[passwordDefinitions[i].name]))
        )

        if (result) {
            println 'The password ' + passwordDefinitions[i].name + ' was successfully created.'
        }
        else {
            println 'The password ' + passwordDefinitions[i].name + ' already exists and will not be updated'
        }
    }

    // Make sure the following variables will not be serialized for the next step which will fail due to store that is not serializable
    store = null
    domain = null

    stage 'Build Probe Dock docker image'
    sh 'pipelines/scripts/probedock-docker-image.sh'

    stage 'Start PostgresSQL'
    withCredentials([[$class: 'StringBinding', credentialsId: POSTGRESSQL_PASSWORD_NAME, variable: DOCKER_POSTGRESQL_PASSWORD]]) {
        println POSTGRES_PASSWORD
        println env.POSTGRES_PASSWORD
        sh 'pipelines/scripts/postgres.sh'
    }

    stage 'Create the database'
    sh 'pipelines/scripts/probedock-create-database.sh'
}