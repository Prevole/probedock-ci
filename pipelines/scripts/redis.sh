#!/usr/bin/env bash

# Make sure Redis is running
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV up --no-recreate -d cache
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm waitCache
