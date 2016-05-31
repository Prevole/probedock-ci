#!/usr/bin/env bash

echo 'Restore dump file'
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps db dropdb -h db -U "$POSTGRES_USER" -f $DUMP_PATH $PROBEDOCK_DATABASE_NAME