#!/usr/bin/env bash

readarray -td. VERSION_ARRAY <<<"${RELEASE_FAMILY}"; declare -p VERSION_ARRAY;

NEXT_VERSION="${VERSION_ARRAY[0]}.$(( ${VERSION_ARRAY[1]} + 1 )).0-SNAPSHOT"

mvn versions:set -DnewVersion="${NEXT_VERSION}"
git add -A
git commit -m "[CICD] Update project version to ${NEXT_VERSION}"