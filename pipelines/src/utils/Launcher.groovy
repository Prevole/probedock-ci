
def launch(operation) {
    println env.PROBEDOCK_ENV


    if (operation.equalsIgnoreCase('Update environment')) {
        jobName = 'CreateOrUpdateEnvironment'
    }

    if (probedockNewCloneRequired) {
        load('ci/pipelines/src/utils/Repos.groovy').cloneProbeDock('ci/images/probedock-base/probedock')
    }

    load('ci/pipelines/src/jobs/' + jobName + '.groovy').executeJob()
}

return this