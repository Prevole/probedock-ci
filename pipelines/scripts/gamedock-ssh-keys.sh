#!/usr/bin/env bash

mkdir -p ~/.ssh

echo $GD_PRIVATE_KEY

touch ~/.ssh/id_rsa

IFS=$'\n'
for LINE in $GD_PRIVATE_KEY; do
  echo $LINE >> ~/.ssh/id_rsa
done

touch ~/.ssh/id_rsa.pub

IFS=$'\n'
for LINE in $GD_PUBLIC_KEY; do
  echo $LINE >> ~/.ssh/id_rsa.pub
done

chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_rsa
chmod 644 ~/.ssh/id_rsa.pub