set -xe

kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"

helm del --purge "${RELEASE_NAME}" || echo '[ERROR] Release does not exist'

kubectl config set-context $(kubectl config current-context) --namespace=default

kubectl delete namespace "${RELEASE_NAME}"

sleep 10