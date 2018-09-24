MVN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
TIMESTAMP=$(date "+%Y%m%d.%H%M%S")
VERSION="${MVN_VERSION}-${TIMESTAMP}-${BUILD_NUMBER}"
VERSION=$(sed 's/-SNAPSHOT//g' <<< ${VERSION})

mvn versions:set -DnewVersion="${VERSION}"

set +x
USERNAME=$(cat /TODO/files/TODO/secrets/docker/.dockercfg | jq '.["https://index.docker.io/v1/"].username' | sed 's/"//g')
PASSWORD=$(cat /TODO/files/TODO/secrets/docker/.dockercfg | jq '.["https://index.docker.io/v1/"].password' | sed 's/"//g')

docker login -u ${USERNAME} -p ${PASSWORD}
set -x

docker pull store/oracle/serverjre:8
gcloud docker -a
mvn -C -B deploy -Ddocker.registry.host='gcr.io' -Pimage
docker rmi $(docker images | grep "${VERSION}" | awk '{ print $3 }')
