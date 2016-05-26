#!/usr/bin/env bash

# Migrate the database
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm task db:migrate