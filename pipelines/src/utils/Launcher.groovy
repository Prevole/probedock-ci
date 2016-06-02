
def launch(operation) {
    println env.PROBEDOCK_ENV

    def jobName = null
    def probedockNewCloneRequired = true

    if (operation.equalsIgnoreCase('Update environment') || operation.equalsIgnoreCase('Create environment')) {
        jobName = 'CreateOrUpdateEnvironment'
        probedockNewCloneRequired = false
    }
    else if (operation.equalsIgnoreCase('Deploy for the first time')) {
        jobName = 'FirstDeploy'
    }
    else if (operation.equalsIgnoreCase('Deploy a new version')) {
        jobName = 'Deploy'
    }
    else if (operation.equalsIgnoreCase('Deploy a new version and load a database dump')) {
        jobName = 'DeployFromDump'
    }
    else if (operation.equalsIgnoreCase('Create an administrator account')) {
        jobName = 'CreateAdmin'
    }
    else if (operation.equalsIgnoreCase('Make a backup of the database')) {
        jobName = 'Backup'
    }

    if (probedockNewCloneRequired) {
        load('ci/pipelines/src/utils/Repos.groovy').cloneProbeDock('ci/images/probedock-base/probedock')
    }

    if (jobName != null) {
        load('ci/pipelines/src/jobs/' + jobName + '.groovy').executeJob()
    }
    else {
        println 'Unknown operation: ' + operation
    }
}

return this