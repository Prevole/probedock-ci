#!/usr/bin/env bash

set +x

echo $POSTGRES_PASSWORD

# Make sure PostgreSQL is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d db
docker-compose -p $PROBEDOCK_ENV run --rm waitdb
