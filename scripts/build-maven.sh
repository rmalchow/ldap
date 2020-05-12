#!/bin/bash

set -e
mvn -Dmaven.repo.local=./m2 package
rm -rf ./m2/de/disk0/ldap