#!/usr/bin/env bash

# Make sure Nginx is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d rp
