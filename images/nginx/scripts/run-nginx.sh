#!/usr/bin/with-contenv bash

sed "s/KIBANA_ADDRESS/$KIBANA/" </etc/nginx-serf/sites/probedock.conf.hbs | \
    sed "s/VIZWEB_ADDRESS/$VIZWEB/" | \
    sed "s/VIZAPI_ADDRESS/$VIZAPI/" > /etc/nginx-serf/sites/probedock.conf.hbs.new

mv /etc/nginx-serf/sites/probedock.conf.hbs.new /etc/nginx-serf/sites/probedock.conf.hbs

cat /etc/nginx-serf/sites/probedock.conf.hbs

/opt/nginx-serf/update.js
exec /usr/sbin/nginx -c /etc/nginx/nginx.conf