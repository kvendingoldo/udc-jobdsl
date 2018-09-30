set -xe

kubectl config set-context $(kubectl config current-context) --namespace=application

[[ -z ${BACKEND_VERSION} ]] && BACKEND_VERSION=$(gcloud container images list-tags gcr.io/university-course/udc-backend-service --limit=1 --format='get(tags)')

cd udc-helm
helm install \
  --set container.version=${BACKEND_VERSION} \
  --name petclinic-application \
  --namespace application \
  udc
