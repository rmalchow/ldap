#!/bin/bash

cd $(dirname $0)
export home=$(pwd)
echo "LDAP home directory is ${home}"
bash stop.sh
mkdir -p ${home}
echo "launching containers ... "
docker-compose -p ldap-prod up -d 