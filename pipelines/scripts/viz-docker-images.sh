#!/usr/bin/env bash

cd ci/images/gamedock/probedock-report

echo 'Build the viz web image'
sudo -E docker build -f client/app/Dockerfile -t probedock/vizweb client/app

echo 'Build the viz api image'
sudo -E docker build -f server/Dockerfile -t probedock/vizapi server
