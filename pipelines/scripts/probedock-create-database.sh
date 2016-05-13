#!/usr/bin/env bash

echo 'Create the database'
docker-compose run --rm --no-deps task rake db:create db:migrate db:seed