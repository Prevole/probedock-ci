#!/usr/bin/env bash

# Make sure PostgreSQL is running
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV up --no-recreate -d db
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm waitDb
