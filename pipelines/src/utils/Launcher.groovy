
def launch(operation) {
    if (operation.equalsIgnoreCase('Update environment')) {
        jobName = 'CreateOrUpdateEnvironment'
    }

    load('pipelines/src/jobs/' + jobName + '.groovy').executeJob()
}

return this