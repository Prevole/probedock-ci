#!/usr/bin/env bash

echo 'Create the database'
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps task db:schema:load db:seed