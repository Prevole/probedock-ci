#!/usr/bin/env bash

echo "Creating the database user"
psql -U "$POSTGRES_USER" -c "CREATE USER '${PROBEDOCK_DATABASE_USERNAME}' PASSWORD '${PROBEDOCK_DATABASE_PASSWORD}'"

echo "Creating the database"
createdb -U "$POSTGRES_USER" -O $PROBEDOCK_DATABASE_USERNAME $PROBEDOCK_DATABASE_NAME