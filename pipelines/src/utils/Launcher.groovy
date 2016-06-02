
def launch(operation) {
    if (operation.equalsIgnoreCase('Update environment')) {
        jobName = 'CreateOrUpdateEnvironment'
    }

    load('pipelines/src/jobs/' + jobName + '.groovy').run()
}

return this