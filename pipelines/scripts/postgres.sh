#!/usr/bin/env bash

chmod 777 ${WORKSPACE}/pipelines/docker/scripts/postgres/init.sh

# Make sure PostgreSQL is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d db
docker-compose -p $PROBEDOCK_ENV run --rm waitDb
