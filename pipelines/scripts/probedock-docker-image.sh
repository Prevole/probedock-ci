#!/usr/bin/env bash

cd probedock

echo 'Build the probedock image'
docker build -t probedock/probedock .