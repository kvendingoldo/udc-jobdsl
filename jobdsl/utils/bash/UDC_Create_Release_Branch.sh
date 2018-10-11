#!/usr/bin/env bash

readarray -td. VERSION_ARRAY <<< "${RELEASE_FAMILY}"; declare -p VERSION_ARRAY;

RELEASE_BRANCH_NAME="release-${VERSION_ARRAY[0]}.${VERSION_ARRAY[1]}"

git checkout -b "${RELEASE_BRANCH_NAME}"
git reset --hard "${COMMIT}"

echo "RELEASE_BRANCH_NAME=${RELEASE_BRANCH_NAME}" > variables.txt