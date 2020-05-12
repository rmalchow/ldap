#!/bin/bash

cd $(dirname $0)
export home=$(pwd)
echo "LDAP home directory is ${home}"
docker-compose -p ldap-dev down
