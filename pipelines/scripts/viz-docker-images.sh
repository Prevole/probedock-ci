#!/usr/bin/env bash

ln -s ./ci/images/gamedock/docker-images/probedock-report-api/Dockerfile ci/images/gamedock/probedock-report/server/Dockerfile
ln -s ./ci/images/gamedock/docker-images/probedock-report-app/Dockerfile ci/images/gamedock/probedock-report/client/app/Dockerfile

echo 'Build the viz web image'
cd ci/images/gamedock/probedock-report/client/app
sudo -E docker build -t probedock/vizweb .

echo 'Build the viz api image'
cd ../../server
sudo -E docker build -t probedock/vizapi .
