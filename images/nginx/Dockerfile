FROM alphahydrae/nginx-serf:0.1.0

COPY sites /etc/nginx-serf/sites

COPY config.yml /etc/nginx-serf/config.yml

VOLUME /usr/src/app/public

ADD bin/run-nginx.sh /etc/services.d/nginx/run