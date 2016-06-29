#!/usr/bin/env bash

# Stop the Viz app containers
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV stop vizWeb
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV stop vizApi

# Cleanup
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV rm -f vizWeb
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV rm -f vizApi
