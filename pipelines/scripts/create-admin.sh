#!/usr/bin/env bash

echo "Create Probe Dock administrator: ${PROBEDOCK_ADMIN_USERNAME} with email: ${PROBEDOCK_ADMIN_EMAIL}"
docker-compose -f docker-compose-app.yml -p $PROBEDOCK_ENV run --rm --no-deps task \
    "users:register[${PROBEDOCK_ADMIN_USERNAME},${PROBEDOCK_ADMIN_EMAIL},${PROBEDOCK_ADMIN_PASSWORD}]" \
    "users:admin[${PROBEDOCK_ADMIN_USERNAME}]"