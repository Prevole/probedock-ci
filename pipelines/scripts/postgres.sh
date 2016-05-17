#!/usr/bin/env bash

echo $WORKSPACE/pipelines/docker/scripts/postgres

# Make sure PostgreSQL is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d db
docker-compose -p $PROBEDOCK_ENV run --rm waitdb
