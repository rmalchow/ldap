#!/bin/bash
set -e
set +x
export TAG=harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest
docker login -u ${CI_EMAIL} -p ${CI_PASSWORD} harbor.rand0m.me
docker build -t ${TAG} -f scripts/Dockerfile .
docker push ${TAG}

docker login -u ${DH_USER} -p ${DH_PW} hub.docker.com
docker tag ${TAG} rmalchow/${CI_PROJECT_NAME}:latest
docker push rmalchow/${CI_PROJECT_NAME}:latest
