#!/usr/bin/env bash

docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps loadDump echo $PROBEDOCK_DATABASE_NAME
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps loadDump echo ${PROBEDOCK_DATABASE_NAME}

echo 'Drop the database'
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps loadDump dropdb -h db -U "$POSTGRES_USER" $PROBEDOCK_DATABASE_NAME

echo 'Create the database'
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps loadDump createdb -h db -U "$POSTGRES_USER" -O $PROBEDOCK_DATABASE_USERNAME $PROBEDOCK_DATABASE_NAME