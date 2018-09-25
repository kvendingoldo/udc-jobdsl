set -x

MVN_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
TIMESTAMP=$(date "+%Y%m%d.%H%M%S")
VERSION="${MVN_VERSION}-${TIMESTAMP}-${BUILD_NUMBER}"
VERSION=$(sed 's/-SNAPSHOT//g' <<< ${VERSION})

mvn versions:set -DnewVersion="${VERSION}"

gcloud docker -a
