#!/usr/bin/env bash

# Make sure Elastic and Kibana is running
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-recreate -d elastic
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm waitEs
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-recreate -d kibana
