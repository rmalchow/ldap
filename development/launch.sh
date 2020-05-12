#!/bin/bash

cd $(dirname $0)
export home=$(pwd)
echo "LDAP home directory is ${home}"
echo "stopping stack ... "
docker-compose -p ldap-dev down
docker-compose -p ldap-dev rm
echo "launching containers ... "
docker-compose -p ldap-dev up -d 