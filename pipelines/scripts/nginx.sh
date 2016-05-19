#!/usr/bin/env bash

# Make sure Nginx is running
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV up --no-recreate -d rp
