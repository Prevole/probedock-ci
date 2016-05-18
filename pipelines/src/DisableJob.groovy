def hudson = hudson.model.Hudson.instance;

this.disableJob(jobName) {
    def job = hudson.model.Hudson.instance.getItem(jobName);
    job.disabled = true
    job.save()

    println 'Job ' + job.name + ' disabled successfully.'
}

return this