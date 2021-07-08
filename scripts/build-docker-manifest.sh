#!/bin/bash
set -e
docker login -u ${CI_EMAIL} -p ${CI_PASSWORD} harbor.rand0m.me

docker pull harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest-x86_64
docker pull harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest-aarch64

docker manifest create \
  harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest \
  harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest-x86_64 \
  harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest-aarch64

docker manifest inspect harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest

docker manifest push harbor.rand0m.me/public/${CI_PROJECT_NAME}:latest
