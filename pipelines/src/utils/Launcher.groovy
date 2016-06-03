/**
 * Job names
 */
this.JOB_CREATE_OR_UPDATE_ENVIRONMENT = 'CreateOrUpdateEnvironment'
this.JOB_FIRST_DEPLOY = 'FirstDeploy'
this.JOB_DEPLOY = 'Deploy'
this.JOB_DEPLOY_FROM_DUMP = 'DeployFromDump'
this.JOB_CREATE_ADMIN = 'CreateAdmin'
this.JOB_BACKUP = 'Backup'

/**
 * Launch a job and allow to clone again the Probe Dock repo for simplification
 *
 * @param jobName The job to launch. It must correspond to a groovy file situated in src/jobs
 * @param newProbeDockClone True to clone Probe Dock again. It will be cloned in images/probedock-base/probedock
 */
def launchJob(jobName, newProbeDockClone = true) {
    if (newProbeDockClone) {
        load('ci/pipelines/src/utils/Repos.groovy').cloneProbeDock('ci/images/probedock-base/probedock')
    }

    if (jobName != null) {
        load('ci/pipelines/src/jobs/' + jobName + '.groovy').executeJob()
    }
    else {
        println 'Unknown operation: ' + operation
    }
}

/**
 * Launch an operation like doing a backup or deploying Probe Dock.
 *
 * The available operations are defined in the OneClick job and are human readable labels.
 *
 * @param operation The operation human readable label
 */
def launch(operation) {
    def jobName = null
    def probedockNewCloneRequired = true

    /**
     * Check if the operation is a known one
     */
    if (operation.equalsIgnoreCase('Update environment') || operation.equalsIgnoreCase('Setup new environment')) {
        jobName = this.JOB_CREATE_OR_UPDATE_ENVIRONMENT
        probedockNewCloneRequired = false
    }
    else if (operation.equalsIgnoreCase('Deploy for the first time')) {
        jobName = this.JOB_FIRST_DEPLOY
    }
    else if (operation.equalsIgnoreCase('Deploy a new version')) {
        jobName = this.JOB_DEPLOY
    }
    else if (operation.equalsIgnoreCase('Deploy a new version and load a database dump')) {
        jobName = this.JOB_DEPLOY_FROM_DUMP
    }
    else if (operation.equalsIgnoreCase('Create an administrator account')) {
        jobName = this.JOB_CREATE_ADMIN
    }
    else if (operation.equalsIgnoreCase('Make a backup of the database')) {
        jobName = this.JOB_BACKUP
    }

    launchJob(jobName, probedockNewCloneRequired)
}

return this