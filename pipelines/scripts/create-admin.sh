#!/usr/bin/env bash

echo 'Create Probe Dock administrator: ${PROBEDOCK_ADMIN_USERNAME} with email ${PROBEDOCK_ADMIN_EMAIL}'
docker-compose -p $PROBEDOCK_ENV run --rm --no-deps task \
    rake \
    "users:register[${PROBEDOCK_ADMIN_USERNAME},${PROBEDOCK_ADMIN_EMAIL},${PROBEDOCK_ADMIN_PASSWORD}]" \
    "users:admin[${PROBEDOCK_ADMIN_USERNAME}]"