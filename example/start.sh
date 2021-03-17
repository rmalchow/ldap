#!/bin/bash
cd $(dirname $0)
export home=$(pwd)
echo "home directory is: ${home}"
./stop.sh
docker network create --subnet 192.168.234.0/28 ldap
echo "starting stack ... "
docker-compose up -d
