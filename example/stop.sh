#!/bin/bash

cd $(dirname $0)
export home=$(pwd)
echo "home directory is ${home}"
echo "stopping stack ... "
docker-compose down
docker-compose rm
docker network rm ldap
