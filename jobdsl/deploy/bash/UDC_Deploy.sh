#!/usr/bin/env bash

set -xe

LATEST_RELEASE_FAMILY=''
RELEASE_ALIAS="udc-backend-${RELEASE_NAME}"

kubectl create namespace "${RELEASE_NAME}"
kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"

if [[ -z ${VERSION} ]]; then
    if [[ ${RELEASE_NAME} != 'production' ]];
    then
        LATEST_RELEASE_FAMILY=$(gcloud container images list \
            --repository=gcr.io/university-course/udc/dev \
            --sort-by=~NAME \
            --format='get(name)' \
            --limit 1)
        VERSION=$(gcloud container images list-tags ${LATEST_RELEASE_FAMILY}/udc-backend-service --limit=1 --format='get(tags)')
    else
        echo '[Error] version is empty'
        exit 0
    fi
fi

if ! gcloud compute addresses list | grep -q "^${RELEASE_ALIAS}"; then
    gcloud compute addresses create --region="${GCP_REGION}" "${RELEASE_ALIAS}"
fi

ENDPOINT=$(gcloud compute addresses list | grep "^${RELEASE_ALIAS}" | awk '{ print $3 }')

cd udc-helm
helm install \
  --set container.version="${VERSION}" \
  --set container.image="${LATEST_RELEASE_FAMILY}/udc-backend-service" \
  --set endpoint="${ENDPOINT}" \
  --name "${RELEASE_NAME}" \
  --namespace "${RELEASE_NAME}" \
  udc



touch ${WORKSPACE}/variables.txt
echo "RELEASE_NAME=${RELEASE_ALIAS}" >> ${WORKSPACE}/variables.txt
echo "VERSION=${VERSION}" >> ${WORKSPACE}/variables.txt
echo "ENDPOINT=${ENDPOINT}" >> ${WORKSPACE}/variables.txt