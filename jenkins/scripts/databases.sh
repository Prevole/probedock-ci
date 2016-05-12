#!/usr/bin/env bash

# Make sure PostgreSQL is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d db
FIRST_CONTAINER=$(docker-compose -p $PROBEDOCK_ENV ps -q db | head -n 1)
docker-compose -p $PROBEDOCK_ENV --rm run waitdb
#docker run --rm --net=${PROBEDOCK_ENV}_probenet --link $FIRST_CONTAINER:db aanand/wait

# Make sure Redis is running
docker-compose -p $PROBEDOCK_ENV up --no-recreate -d cache
FIRST_CONTAINER=$(docker-compose -p $PROBEDOCK_ENV ps -q cache | head -n 1)
docker run --rm --net=${PROBEDOCK_ENV}_probenet --link $FIRST_CONTAINER:cache aanand/wait