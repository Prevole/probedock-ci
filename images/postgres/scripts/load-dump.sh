#!/usr/bin/env bash

echo 'Restore dump file'
psql -h db -U "${POSTGRES_USER}" -f ${DUMP_PATH} ${PROBEDOCK_DATABASE_NAME}