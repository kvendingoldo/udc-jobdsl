#!/bin/bash

set -xe

kubectl create namespace "${RELEASE_NAME}"
kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"


[[ -z ${VERSION} ]]


# TODO: reserve IP


cd udc-helm
helm install \
  --set container.version="${VERSION}" \
  --name "${RELEASE_NAME}" \
  --namespace "${RELEASE_NAME}" \
  udc
