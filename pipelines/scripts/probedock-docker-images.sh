#!/usr/bin/env bash

cd ci/images

echo 'Build the Probe Dock base image'
docker build -t probedock/probedock-base probedock-base

echo 'Build the Probe Dock backend image'
docker build -t probedock/probedock-app probedock-app

echo 'Build the Probe Dock job image'
docker build -t probedock/probedock-job probedock-job
