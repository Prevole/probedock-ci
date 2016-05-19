#!/usr/bin/env bash

# Compile the assets and templates
docker-compose -p $PROBEDOCK_ENV run --no-deps assets rake assets:precompile assets:clean
docker-compose -p $PROBEDOCK_ENV run --no-deps assets rake templates:precompile static:copy
