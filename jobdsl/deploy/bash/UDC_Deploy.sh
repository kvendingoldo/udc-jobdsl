#!/bin/bash

set -xe

kubectl create namespace "${RELEASE_NAME}"
kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"

[[ -z ${VERSION} ]] && VERSION=$(gcloud container images list-tags gcr.io/university-course/udc-backend-service --limit=1 --format='get(tags)')

cd udc-helm
helm install \
  --set container.version="${VERSION}" \
  --name "${RELEASE_NAME}" \
  --namespace "${RELEASE_NAME}" \
  udc
