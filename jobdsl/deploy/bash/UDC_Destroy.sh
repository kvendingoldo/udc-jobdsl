#!/usr/bin/env bash

set -xe

RELEASE_ALIAS="udc-backend-${RELEASE_NAME}"

kubectl config set-context "$(kubectl config current-context)" --namespace="${RELEASE_NAME}"

helm del --purge "${RELEASE_NAME}" || echo '[ERROR] Release does not exist'

kubectl config set-context "$(kubectl config current-context)" --namespace=default

kubectl delete namespace "${RELEASE_NAME}" || echo '[ERROR] Namespace does not exist'

if gcloud compute addresses list --filter="name~'${RELEASE_ALIAS}'" --format='get(name)'; then
    gcloud compute addresses delete --region="${GCP_REGION}" "${RELEASE_ALIAS}" || echo 'IP does not exist'
fi

sleep 25
