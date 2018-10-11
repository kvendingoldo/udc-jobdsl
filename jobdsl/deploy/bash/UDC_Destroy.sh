set -xe

kubectl config set-context $(kubectl config current-context) --namespace="${RELEASE_NAME}"

helm del --purge "${RELEASE_NAME}" || echo 'Release does not exist'

kubectl delete namespace "${RELEASE_NAME}"

sleep 10
