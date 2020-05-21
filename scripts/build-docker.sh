#!/bin/bash
set -e
echo "build docker image ... " 
#export TAG=docker.sly.io/${CI_PROJECT_PATH}/ldap
docker build -t rmalchow/ldap:latest -f scripts/Dockerfile .
#docker push ${TAG}
echo "push docker image ... " 
docker login -u "${DH_USER}" -p "${DH_PW}"
docker push rmalchow/ldap:latest
