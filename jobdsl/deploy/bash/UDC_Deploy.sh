#!/usr/bin/env bash

set -xe

RELEASE_ALIAS="udc-backend-${RELEASE_NAME}"
RELEASE_FAMILY=''

kubectl create namespace "${RELEASE_NAME}"
kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"

if [[ -z ${VERSION} ]]; then
    if [[ ${RELEASE_NAME} != 'production' ]]; then
        IMAGE_FAMILY=$(gcloud container images list \
            --repository=gcr.io/university-course/udc/dev \
            --sort-by=~NAME \
            --format='get(name)' \
            --limit 1)
        VERSION=$(gcloud container images list-tags ${IMAGE_FAMILY}/udc-backend-service --limit=1 --format='get(tags)')
        RELEASE_FAMILY=$(echo ${IMAGE_FAMILY} | sed -e 's/.*\///g')
    else
        echo '[Error] version is empty'
        exit 1
    fi
else
  RELEASE_FAMILY=$(echo ${VERSION} | sed -e 's/-.*//')
fi

if ! gcloud compute addresses list | grep -q "^${RELEASE_ALIAS}"; then
    gcloud compute addresses create --region="${GCP_REGION}" "${RELEASE_ALIAS}"
fi

ENDPOINT=$(gcloud compute addresses list | grep "^${RELEASE_ALIAS}" | awk '{ print $3 }')

cd udc-helm
helm install \
  --set container.version="${VERSION}" \
  --set container.image="gcr.io/university-course/udc/dev/${RELEASE_FAMILY}/udc-backend-service" \
  --set endpoint="${ENDPOINT}" \
  --name "${RELEASE_NAME}" \
  --namespace "${RELEASE_NAME}" \
  udc

{
  echo "RELEASE_NAME=${RELEASE_ALIAS}"
  echo "VERSION=${VERSION}"
  echo "ENDPOINT=${ENDPOINT}"
} > "${WORKSPACE}/variables.txt"
