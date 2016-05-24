#!/usr/bin/env bash

cd images/probedock-base

echo 'Build the Probe Dock base image'
docker build -t probedock/probedock-base .

cd ../images/probedock-app

echo 'Build the Probe Dock backend image'
docker build -t probedock/probedock-app .

cd ../images/probedock-job

echo 'Build the Probe Dock job image'
docker build -t probedock/probedock-job .
