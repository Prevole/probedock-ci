#!/usr/bin/env bash

cd images

echo 'Build the Probe Dock base image'
docker build -t probedock/probedock-base images/probedock-base

echo 'Build the Probe Dock backend image'
docker build -t probedock/probedock-app images/probedock-app

echo 'Build the Probe Dock job image'
docker build -t probedock/probedock-job images/probedock-job
