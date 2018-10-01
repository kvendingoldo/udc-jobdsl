#!/bin/bash

set -xe

kubectl config set-context $(kubectl config current-context) --namespace=application

[[ -z ${VERSION} ]] && VERSION=$(gcloud container images list-tags gcr.io/university-course/udc-backend-service --limit=1 --format='get(tags)')

cd udc-helm
helm install \
  --set container.version=${VERSION} \
  --name petclinic-application \
  --namespace application \
  udc
