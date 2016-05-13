#!/usr/bin/env bash

echo 'Create the database'
docker-compose -p $PROBEDOCK_ENV run --rm --no-deps task rake db:create db:migrate db:seed