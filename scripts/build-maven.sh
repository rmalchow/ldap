#!/bin/bash

set -e

cd $(dirname ${0})/src

mvn -Dmaven.repo.local=./m2 package
rm -rf ../m2/de/disk0/ldap
