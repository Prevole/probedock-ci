## How to

1. Make sure you have the following folders (which will be used as volumes in Docker)

```
/data
/jenkins
```

In fact, the `/data` can be placed anywhere on the file system and you will have to configure it during the setup of
an environment for Probe Dock. This setup has to be done in Jenkins job `CreateOrUpdateEnvironment`.

2. Make sure the folder `/jenkins` has the correct rights.

```
# Create the dedicated user on Docker host. This will let the Jenkins container user to write in the data volume
sudo addgroup -g 1000 jenkins
sudo adduser -G jenkins -D -H -u 2000 jenkins

# Make sure the folder /jenkins is owned by user and group Jenkins
sudo chown jenkins:jenkins /jenkins
```

3. Make sure Docker is installed and running

4. Run the following script (at the root of this directory). This script will build some images used in the Probe Dock
infra and also start the Jenkins instance to manage the infra.

```
./start.sh
```

5. Once the script has ended, you can open your browser and access to <jenkinsHost>:8080. You will get access to the following jobs:

* `Backup`: Allow to make a backup of the PostgreSQL database. The Probe Dock application will be stopped before the backup and restarted after it.
* `CreateAdmin`: Allow to create a new admin account on Probe Dock. **Hint**: You can use this job to create a new admin if you loose the previous admin password.
* `CreateOrUpdateEnvironment`: Create new Probe Dock environment or update an existing one. This allow to change the configuration. **Hint**: Credentials are stored through `credentials plugin` and therefore can be updated through the plugin interface. **Remark**: Take care that updating a password will not change the password in the infra.
* `Deploy`: The deploy job is used to deploy specific version of Probe Dock. This job is always used once the `FirstDeploy` has been run once.
* `DeployFromDump`: Same as deploy job. In addition, you can choose a PostgreSQL dump file (previously placed in `/jenkins/dumps`) to fill the database with it.
* `FirstDeploy`: The first deploy job allow to setup for the first time an environment. If an environment is already runnin, the job will fail with strange errors.

6. To create your first `default` environment, you have to run the job `CreateOrUpdateEnvironment` and follow the Jenkins job instructions. You will have to fill several configuraiton value and to choose if you want to first deploy or not. Check the box and that's it.

7. If everything worked well, you will be able to access your `default` environment through `<host>:3000`