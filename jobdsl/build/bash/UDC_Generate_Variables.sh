#!/usr/bin/env bash

touch ${WORKSPACE}/variables.txt

echo "COMMIT=${GIT_COMMIT}" >> ${WORKSPACE}/variables.txt
echo "BRANCH=${GIT_BRANCH}" >> ${WORKSPACE}/variables.txt