#!/usr/bin/env bash

cp ci/images/gamedock/docker-images/probedock-report-api/Dockerfile ci/images/gamedock/probedock-report/server/Dockerfile
cp ci/images/gamedock/docker-images/probedock-report-app/Dockerfile ci/images/gamedock/probedock-report/client/app/Dockerfile

echo 'Build the viz web image'
cd ci/images/gamedock/probedock-report/client/app
sed s/tmp\/app/./ Dockerfile > Dockerfile
sudo -E docker build -t probedock/vizweb .

echo 'Build the viz api image'
cd ../../server
sed s/tmp\/server/./ Dockerfile > Dockerfile
sudo -E docker build -t probedock/vizapi .
