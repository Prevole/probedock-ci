#!/usr/bin/env bash

docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps loadDump /scripts/load-dump.sh