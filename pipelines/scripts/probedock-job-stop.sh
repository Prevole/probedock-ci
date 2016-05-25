#!/usr/bin/env bash

# Stop the Probe Dock app containers
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV stop -t 30 job

# Cleanup
docker-compose -f docker-compose-app.yml -p default rm -f job
