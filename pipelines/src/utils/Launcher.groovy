
def launch(operation) {
    if (operation.equalsIgnoreCase('Update environment')) {
        jobName = 'CreateOrUpdateEnvironment'
    }

    load('pipelines/jobs/' + jobName + '.groovy').run()
}

return this