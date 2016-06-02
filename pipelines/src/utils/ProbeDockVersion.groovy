
/**
 *
 */
def version() {
    // Pointing to the version file
    def File versionsFile = new File('/envs/.probeDockVersions')

    // Properties to store the Probe Dock version by environment
    def Properties versionsProperties = new Properties()

    // Check if an environment is defined
    if (env.PROBEDOCK_ENV) {

        // Check if the file exists
        if (versionsFile.exists()) {
            // Load the properties and try to get the version for the Probe Dock env
            versionsProperties.load(new FileInputStream(versionsFile))
            env.PROBEDOCK_VERSION = versionsProperties.get(env.PROBEDOCK_ENV)
        }
    }

    // Ask the user for the Probe Dock version
    env.PROBEDOCK_VERSION = input(
        message: 'Choose the Probe Dock version you want to deploy',
        parameters: [[
            $class: 'StringParameterDefinition',
            defaultValue: env.PROBEDOCK_VERSION ? env.PROBEDOCK_VERSION : 'master',
            description: 'Probe Dock Git reference',
            name: 'PROBEDOCK_VERSION'
        ]]
    )

    println env.PROBEDOCK_VERSION

    if (env.PROBEDOCK_ENV) {
        versionsProperties.set(env.PROBEDOCK_ENV, env.PROBEDOCK_VERSION)
        versionsProperties.store(new FileOutputStream(versionsFile))
    }
}

return this