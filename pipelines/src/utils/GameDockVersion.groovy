package utils

/**
 *
 */
def version() {
    // Pointing to the version file
    def File versionsFile = new File('/envs/.gameDockVersions')

    // Properties to store the Probe Dock version by environment
    def Properties versionsProperties = new Properties()

    // Check if the file exists
    if (versionsFile.exists()) {
        // Load the properties and try to get the version for the Probe Dock env
        versionsProperties.load(new FileInputStream(versionsFile))
        env.GAMEDOCK_VERSION = versionsProperties.getProperty(env.PROBEDOCK_ENV)

        if (env.GAMEDOCK_VERSION == null) {
            env.GAMEDOCK_VERSION = 'master'
        }
    }

    // Ask the user for the Probe Dock version
    env.GAMEDOCK_VERSION = input(
        message: 'Choose the Game Dock version you want to deploy',
        parameters: [[
            $class: 'hudson.model.StringParameterDefinition',
            defaultValue: env.GAMEDOCK_VERSION ? env.GAMEDOCK_VERSION : 'master',
            description: 'Game Dock Git reference',
            name: 'GAMEDOCK_VERSION'
        ]]
    )

    versionsProperties.setProperty(env.PROBEDOCK_ENV, env.GAMEDOCK_VERSION)
    versionsProperties.store(new FileOutputStream(versionsFile), '')
}

return this