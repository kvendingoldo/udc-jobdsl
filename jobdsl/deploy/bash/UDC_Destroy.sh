set -xe

kubectl config set-context $(kubectl config current-context) --namespace=application

helm del --purge petclinic-application || echo 'Release does not exist'

sleep 5
