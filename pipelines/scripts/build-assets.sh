#!/usr/bin/env bash

# Compile the assets and templates
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --no-deps assets assets:precompile assets:clean
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --no-deps assets templates:precompile static:copy
