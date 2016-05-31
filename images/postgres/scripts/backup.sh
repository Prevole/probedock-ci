#!/usr/bin/env bash

sh -c 'exec pg_dump -h ${ENV}_db -p 5432 -U postgres ${PROBEDOCK_DATABASE_NAME}' | gzip > /backups/${ENV}_${DATE}_db.sql.gz || exit 1