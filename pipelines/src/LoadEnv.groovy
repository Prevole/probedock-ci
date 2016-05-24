def setupEnv(env, propertyFile) {
    def File envFile = new File(propertyFile)
    def envExists = envFile.exists()

    /**
     * Load the properties from the env file
     */
    def Properties envProperties = new Properties()
    if (envExists) {
        envProperties.load(new FileInputStream(envFile))
    }

    def propertyNames = properties.keys()

    for (int i = 0; i < propertyNames.length; i++) {
        env[propertyNames[i]] = properties.getProperty(propertyNames[i])
    }
}

return this