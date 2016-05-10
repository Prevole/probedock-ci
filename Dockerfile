FROM jenkinsci/jenkins

USER root

#RUN ssh-keyscan -H github.com >> ~/.ssh/known_hosts

RUN apt-get update ; apt-get install docker.io -y \
    && curl -L https://github.com/docker/compose/releases/download/1.7.1/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose \
    && gpasswd -a jenkins users

USER jenkins

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
