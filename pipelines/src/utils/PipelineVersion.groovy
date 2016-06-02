/**
 * Setup the environment variables from a property file
 *
 * @param env The environment to setup
 * @param propertyFile The property file that stores the configuration for the environment
 */
def version() {
    def ipt = input(
        message: 'Choose the Probe Dock version you want to deploy',
        parameters: [[
            $class: 'StringParameterDefinition',
            defaultValue: 'master',
            description: 'Probe Dock Git reference',
            name: 'PROBEDOCK_VERSION'
        ]]
    )

    println ipt

//    def File envFile = new File(propertyFile)
//    def envExists = envFile.exists()
//
//    /**
//     * Load the properties from the env file
//     */
//    def Properties envProperties = new Properties()
//    if (envExists) {
//        envProperties.load(new FileInputStream(envFile))
//    }
//
//    def propertyNames = envProperties.keySet().toArray(new String[0])
//
//    for (int i = 0; i < propertyNames.size(); i++) {
//        env[propertyNames[i]] = envProperties.getProperty(propertyNames[i])
//    }
//
//    /**
//     * Prepare the date for backup file names
//     */
//    sh "echo -n \$(date '+%Y_%m_%d_%H_%M_%S') > date"
//    env.PROBEDOCK_DATE = readFile 'date'
}

return this