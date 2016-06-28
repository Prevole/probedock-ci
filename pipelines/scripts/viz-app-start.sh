#!/usr/bin/env bash

# Start the Viz app containers
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-deps -d vizapi
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-deps -d vizweb

