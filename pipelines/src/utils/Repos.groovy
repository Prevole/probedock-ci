/**
 * Clone the CI repo
 */
def cloneCi() {
    checkout(
        changelog: false,
        poll: false,
        scm: [
            $class: 'GitSCM',
            branches: [[name: env.PROBEDOCK_CI_VERSION ? env.PROBEDOCK_CI_VERSION : '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'ci' ],
                [$class: 'WipeWorkspace']
            ],
            submoduleCfg: [],
            userRemoteConfigs: [[
                refspec: env.PROBEDOCK_CI_VERSION ? env.PROBEDOCK_CI_VERSION : 'master',
                url: env.REPO_CI
            ]]
        ]
    )
}

/**
 * Clone the Probe Dock repo
 */
def cloneProbeDock(path = null) {

}

def cloneGameDock(path = null) {
    // Reference to the CI file in Probe Dock repo
    def File privKey = new File('~/.ssh/id_rsa')

    if (!privKey.exists()) {
        def keys = input(
            message: 'Configure the Game Dock SSH deployment key',
            parameters: [[
                $class: 'TextParameterDefinition',
                defaultValue: '',
                description: 'Give the private SSH key',
                name: 'PRIVATE_KEY'
            ], [
                $class: 'TextParameterDefinition',
                defaultValue: '',
                description: 'Give the public SSH key',
                name: 'PUBLIC_KEY'
            ]]
        )

        env.GD_PRIVATE_KEY = keys.PRIVATE_KEY

        println env.GD_PRIVATE_KEY

        sh 'ci/pipelines/scripts/gamedock-ssh-keys.sh'

        env.GD_PRIVATE_KEY = ''
    }

    checkout(
        changelog: false,
        poll: false,
        scm: [
            $class: 'GitSCM',
            branches: [[name: env.GAMEDOCK_VERSION ? env.GAMEDOCK_VERSION : '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
                [$class: 'WipeWorkspace'],
                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'gamedock']
            ],
            submoduleCfg: [],
            userRemoteConfigs: [[
                refspec: env.GAMEDOCK_VERSION ? env.GAMEDOCK_VERSION : 'master',
                url: 'git@bitbucket.org:probedock/game-dock.git'
            ]]
        ]
    )
}

return this