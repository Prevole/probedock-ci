#!/usr/bin/with-contenv bash

sed "s/KIBANA_ADDRESS/$VIZWEB/" </etc/nginx-serf/sites/probedock.conf.hbs | \
    sed "s/VIZAPI_ADDRESS/$VIZAPI/" > /etc/nginx-serf/sites/probedock.conf.hbs.new

mv /etc/nginx-serf/sites/probedock.conf.hbs.new /etc/nginx-serf/sites/probedock.conf.hbs

/opt/nginx-serf/update.js
exec /usr/sbin/nginx -c /etc/nginx/nginx.conf