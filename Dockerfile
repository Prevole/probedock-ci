FROM jenkinsci/jenkins

USER root

#RUN ssh-keyscan -H github.com >> ~/.ssh/known_hosts

RUN apt-get update ; apt-get install docker.io -y

USER jenkins

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
