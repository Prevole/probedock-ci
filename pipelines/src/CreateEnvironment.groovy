

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
import java.lang.StringBuilder

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
    // Clone the pipelines repo
    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Prevole/probedock-ci']]]

    def Passwords = load 'pipelines/src/Passwords.groovy'

    /**
     * This step will ask the Probe Dock deploy for several passwords that will be used to setup the database and such things.
     *
     * All the passwords will be stored through the Credentials plugin in a secure way.
     */
    stage 'Create a new Probe Dock environment'

    // Definitions
    def passwordAlphabet = join(('A'..'Z')+('a'..'z')+('0'..'9'))
    def keysAlphabet = join(('A'..'Z')+('a'..'z')+('0'..'9'))
    def passwordLength = 32
    def keysLength = 128

    // Define the parameters
    def parametersDefinitions = [[
        name: 'ENV',
        humanName: 'Environment name',
        description: 'The environment name.',
        default: 'default',
        password: false
    ], [
        name: 'PROBEDOCK_LOG_LEVEL',
        humanName: 'Log level',
        description: 'Rails application log level.',
        default: 'info',
        password: false
    ], [
        name: 'PROBEDOCK_MAIL_ADDRESS',
        humanName: 'SMTP server host',
        description: 'SMTP address to send e-mails.',
        default: '',
        password: false
    ], [
        name: 'PROBEDOCK_MAIL_PORT',
        humanName: 'SMTP server port',
        description: 'SMTP port to send e-mails.',
        default: '587',
        password: false
    ], [
        name: 'PROBEDOCK_MAIL_DOMAIN',
        humanName: 'SMTP domain',
        description: 'SMTP domain to send e-mails. (Used in EHLO SMTP command).',
        default: '',
        password: false
    ], [
        name: 'PROBEDOCK_MAIL_AUTHENTICATION',
        humanName: 'SMTP authentication',
        description: 'SMTP authentication method.',
        default: 'plain',
        password: false
    ], [
        name: Passwords.PROBEDOCK_SMTP_USER_BASE_NAME,
        humanName: 'SMTP user name',
        description: 'The SMTP user used to send emails from Probe Dock',
        default: '',
        password: true
    ], [
        name: Passwords.PROBEDOCK_SMTP_PASSWORD_BASE_NAME,
        humanName: 'SMTP Password',
        description: 'The SMTP password',
        default: '',
        password: true
    ], [
        name: 'PROBEDOCK_MAIL_FROM',
        humanName: 'SMTP sender address',
        description: 'From address for e-mails sent by Probe Dock.',
        default: '',
        password: false
    ], [
        name: 'PROBEDOCK_MAIL_FROM_NAME',
        humanName: 'SMTP sender name',
        description: 'From address name for e-mails sent by Probe Dock.',
        default: '',
        password: false
    ], [
        name: 'PROBEDOCK_APP_PROTOCOL',
        humanName: 'Application protocol',
        description: 'External address protocol (http or https).',
        choices: 'https\nhttp',
        password: false
    ], [
        name: 'PROBEDOCK_APP_HOST',
        humanName: 'Application host',
        description: 'External address host (e.g. app.example.com)',
        default: '',
        password: false
    ], [
        name: 'PROBEDOCK_APP_PORT',
        humanName: 'Application port',
        description: 'External address port (e.g. 80, 443).',
        default: '443',
        password: false
    ], [
        name: 'PROBEDOCK_UNICORN_WORKERS',
        humanName: 'Number of unicorn workers',
        description: 'Number of Unicorn workers (Rails application instances) to run per application container.',
        default: '3',
        password: false
    ], [
        name: 'PROBEDOCK_DOCKER_APP_CONTAINERS',
        humanName: 'Number of application containers',
        description: 'Number of application containers to run. Note that each application container might itself run multiple workers depending on PROBEDOCK_UNICORN_WORKERS.',
        default: '3',
        password: false
    ], [
        name: 'PROBEDOCK_DOCKER_JOB_CONTAINERS',
        humanName: 'Number of job containers',
        description: 'Number of background job containers to run.',
        default: '3',
        password: false
    ], [
        name: 'PROBEDOCK_DOCKER_WEB_CONTAINER_PORT',
        humanName: 'Docker web container port',
        description: 'Host port to expose the web container on. Must be different for each environment. It will be used for port mapping.',
        default: '3000',
        password: false
    ], [
        name: Passwords.POSTGRESSQL_PASSWORD_BASE_NAME,
        humanName: 'PostgreSQL root password',
        description: 'The root password for PostgreSQL',
        default: strGenerator(passwordAlphabet, passwordLength),
        password: true
    ], [
        name: Passwords.PROBEDOCK_DB_PASSWORD_BASE_NAME,
        humanName: 'Probe Dock database password',
        description: 'The password for Probe Dock PostgreSQL database.',
        default: strGenerator(passwordAlphabet, passwordLength),
        password: true
    ], [
        name: Passwords.PROBEDOCK_SECRET_KEY_BASE_NAME,
        humanName: 'Secret key',
        description: 'The secret key base',
        default: strGenerator(keysAlphabet, keysLength),
        password: true
    ], [
        name: Passwords.PROBEDOCK_JWT_SECRET_BASE_NAME,
        humanName: 'JSON Web Token secret',
        description: 'The JWT secret',
        default: strGenerator(keysAlphabet, keysLength),
        password: true
    ]]

    def inputParameters = []

    // WORKAROUND: Seems the pipeline plugin is buggy with .each, ... See: https://issues.jenkins-ci.org/browse/JENKINS-26481
    for (int i = 0; i < parametersDefinitions.size(); i++) {
        if (parametersDefinitions[i].containsKey('choices')) {
            println parametersDefinitions[i].name
            inputParameters.add([
                $class: 'ChoiceParameterDefinition',
                choices: parametersDefinitions[i].choices,
                description: parametersDefinitions[i].description,
                name: parametersDefinitions[i].humanName
            ])

        }
        else {
            inputParameters.add([
                $class: 'StringParameterDefinition',
                defaultValue: parametersDefinitions[i].default,
                description: parametersDefinitions[i].description,
                name: parametersDefinitions[i].humanName
            ])
        }
    }

    // Ask the user for initial passwords
    def filledParameters = input(
        message: 'Define passwords. Attention: You MUST store the credentials information in a secure way.',
        parameters: inputParameters
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

    def env = filledParameters[parametersDefinitions[0].humanName]

    StringBuilder sb = new StringBuilder()
    // Store each passwords
    for (int i = 0; i < parametersDefinitions.size(); i++) {
        // Check if the parameters must be stored as a password
        if (parametersDefinitions[i].password) {
            def result = store.addCredentials(
                domain,
                new StringCredentialsImpl(
                    CredentialsScope.GLOBAL,
                    env + '-' + parametersDefinitions[i].name,
                    '[' + env + '] ' + parametersDefinitions[i].description,
                    Secret.fromString(filledParameters[parametersDefinitions[i].humanName])
                )
            )

            if (result) {
                println 'The password ' + parametersDefinitions[i].humanName + ' was successfully created.'
            } else {
                println 'The password ' + parametersDefinitions[i].humanName + ' already exists and will not be updated'
            }
        }

        // The environment name is not handle like the other parameters
        else if (i > 0) {
            sb.append(parametersDefinitions[i].name).append('=').append(filledParameters[parametersDefinitions[i].humanName]).append('\n')
        }
    }

    def propertyFile = new File('/envs/' + env)

    propertyFile.write sb.toString()

    // Make sure the following variables will not be serialized for the next step which will fail due to store that is not serializable
    store = null
    domain = null
}