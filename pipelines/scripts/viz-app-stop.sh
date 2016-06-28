#!/usr/bin/env bash

# Stop the Viz app containers
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV stop vizweb
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV stop vizapi

# Cleanup
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV rm -f vizweb
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV rm -f vizapi
