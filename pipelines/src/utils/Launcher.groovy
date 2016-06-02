
def launch(operation) {
    println env.PROBEDOCK_ENV


    if (operation.equalsIgnoreCase('Update environment')) {
        jobName = 'CreateOrUpdateEnvironment'
    }

    load('ci/pipelines/src/jobs/' + jobName + '.groovy').executeJob()
}

return this