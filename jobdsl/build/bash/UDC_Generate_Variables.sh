#!/usr/bin/env bash

{
  echo "COMMIT=${GIT_COMMIT}"
  echo "BRANCH=${GIT_BRANCH}"
} > "${WORKSPACE}/variables.txt"
