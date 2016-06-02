/**
 *
 */
def version() {
    // Reference to the CI file in Probe Dock repo
    def File ciFile = new File(env.PROBEDOCK_CHECKOUT_PATH + '.probeDockCi')

    if (ciFile.exists()) {
        def Properties ciProperties = new Properties()
        ciProperties.load(new FileInputStream(ciFile))
        env.PROBEDOCK_CI_VERSION = ci.get('ciVersion')
    }
    else {
        env.PROBEDOCK_CI_VERSION = env.PROBEDOCK_CI_DEFAULT_VERSION
    }
}

return this