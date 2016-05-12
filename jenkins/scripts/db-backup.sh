#!/usr/bin/env bash

# Backup data from the previously deployed version.
mkdir -p /var/lib/$PROBEDOCK_ENV/backup

if [ -f /var/lib/$PROBEDOCK_ENV/current/docker-compose.yml ]; then
  cd /var/lib/$PROBEDOCK_ENV/current

  FIRST_DB_CONTAINER=$(docker-compose -p ${PROBEDOCK_ENV} ps -q db|head -n 1)
  if [ -n "$FIRST_DB_CONTAINER" ]; then

    DATE=$(date '+%Y_%m_%d_%H_%M_%S')
    DB_IMAGE=$(docker inspect -f "{{.Image}}" $FIRST_DB_CONTAINER)

    echo "Dumping database to /var/lib/probedock-trial/backup/probedock_trial_${DATE}_db.sql.gz..."
    set +x
    sudo docker run --rm --link $FIRST_DB_CONTAINER:postgres -e "PGPASSWORD=$POSTGRES_PASSWORD" "$DB_IMAGE" sh -c 'exec pg_dump -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U postgres probedock' | gzip > /var/lib/probedock-trial/backup/probedock_trial_${DATE}_db.sql.gz || exit 1
    set -x

    echo "Dumping user registration data to /var/lib/probedock-trial/backup/probedock_trial_${DATE}_registrations.json.gz..."
    sudo docker-compose -p probedocktrial run --rm task rake registrations:dump | gzip > /var/lib/probedock-trial/backup/probedock_trial_${DATE}_registrations.json.gz || exit 2
  fi
fi
