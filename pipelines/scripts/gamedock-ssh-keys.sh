#!/usr/bin/env bash

mkdir -p ~/.ssh

echo $GD_PRIVATE_KEY

touch ~/.ssh/id_rsa

IFS=$'\n'
for LINE in $GD_PRIVATE_KEY; do
  echo $LINE >> ~/.ssh/id_rsa
done

#echo $GD_PRIVATE_KEY > ~/.ssh/id_rsa
#echo $GD_PUBLIC_KEY > ~/.ssh/id_rsa.pub

chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_rsa