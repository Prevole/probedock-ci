#!/usr/bin/env bash

# Make sure Nginx is running
sudo docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-recreate -d rp
