
def launch(operation) {
    if (operation.equalsIgnoreCase('Update environment')) {
        job = load('ci/pipelines/jobs/CreateOrUpdateEnvironment.groovy')
    }

    job.run()
}

return this