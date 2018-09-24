#!/bin/bash

set -x

[[ -z ${BACKEND_VERSION} ]] && BACKEND_VERSION=$(gcloud container images list-tags gcr.io/university-course/TODO --limit=1 --format='get(tags)')

cd udc-helm
helm install --set backend.component.version=${BACKEND_VERSION} --set self-healing.component.version=${BACKEND_VERSION} --set web-console-backend.component.version=${WEB_CONSOLE_VERSION} --set web-console-static.component.version=${WEB_CONSOLE_VERSION} --values /TOOD/files/TODO/secrets/helm/values.yaml --name 1-0-0 --namespace application todo
