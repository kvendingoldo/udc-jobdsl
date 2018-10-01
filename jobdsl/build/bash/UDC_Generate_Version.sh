#!/bin/bash

set -x

if [[ "${GENERATED_VERSION_TYPE}" == 'precommit' ]]; then
  MVN_VERSION='0.0.0'
elif [[ "${GENERATED_VERSION_TYPE}" == 'custom' ]]; then
  MVN_VERSION='0.1.0'
else
  MVN_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
  MVN_VERSION=${MVN_VERSION%'-SNAPSHOT'}
fi

TIMESTAMP=$(date "+%Y%m%d.%H%M%S")
VERSION="${MVN_VERSION}-${TIMESTAMP}-${BUILD_NUMBER}"

mvn versions:set -DnewVersion="${VERSION}"
echo -n "VERSION=${VERSION}" > version.properties
