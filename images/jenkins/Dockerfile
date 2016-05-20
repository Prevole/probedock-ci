FROM jenkinsci/jenkins

USER root

RUN apt-get update ; apt-get install docker.io -y \
    && curl -L https://github.com/docker/compose/releases/download/1.7.1/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose \
    && apt-get install -y sudo \
    && rm -rf /var/lib/apt/lists/*

RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers

COPY plugins.txt /usr/share/jenkins/plugins.txt

RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

COPY config/scriptApproval.xml /usr/share/jenkins/ref/scriptApproval.xml

COPY jobs /usr/share/jenkins/ref/jobs/

COPY scripts/wait-for-it.sh .

RUN usermod -u 2000 jenkins \
    && gpasswd -a jenkins users

USER jenkins

VOLUME /envs

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
