#!/bin/bash

cd $(dirname $0)
export home=$(pwd)
echo "LDAP home directory is ${home}"
echo "stopping stack ... "
docker-compose -p ldap-prod down
docker-compose -p ldap-prod rm
