mvn versions:set -DnewVersion="${BUILD_VERSION}"

set +x
USERNAME=$(cat /TODO/files/TODO/secrets/docker/.dockercfg | jq '.["https://index.docker.io/v1/"].username' | sed 's/"//g')
PASSWORD=$(cat /TODO/files/TODO/secrets/docker/.dockercfg | jq '.["https://index.docker.io/v1/"].password' | sed 's/"//g')

docker login -u ${USERNAME} -p ${PASSWORD}
set -x

docker pull store/oracle/serverjre:8
gcloud docker -a
mvn -C -B deploy -Ddocker.registry.host='gcr.io' -Pimage
docker rmi $(docker images | grep "${BUILD_VERSION}" | awk '{ print $3 }')
