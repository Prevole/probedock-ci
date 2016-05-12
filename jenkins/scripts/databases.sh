#!/usr/bin/env bash

# Make sure PostgreSQL is running
sudo docker-compose -p $PROBEDOCK_ENV up --no-recreate -d db
FIRST_CONTAINER=$(sudo docker-compose -p $PROBEDOCK_ENV ps -q db | head -n 1)
sudo docker run --rm --link $FIRST_CONTAINER:db aanand/wait

# Make sure Redis is running
sudo docker-compose -p $PROBEDOCK_ENV up --no-recreate -d cache
FIRST_CONTAINER=$(sudo docker-compose -p $PROBEDOCK_ENV ps -q cache | head -n 1)
sudo docker run --rm --link $FIRST_CONTAINER:cache aanand/wait