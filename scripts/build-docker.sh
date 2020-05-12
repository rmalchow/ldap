#!/bin/bash
set -e
#export TAG=docker.sly.io/${CI_PROJECT_PATH}/ldap
docker build -t rmalchow/ldap:latest -f scripts/Dockerfile .
#docker push ${TAG}
docker login -u "${DH_USER}" -p "${DH_PW}"
docker push rmalchow/ldap:latest
