set -x

MVN_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
MVN_VERSION=${MVN_VERSION%'-SNAPSHOT'}
TIMESTAMP=$(date "+%Y%m%d.%H%M%S")
VERSION="${MVN_VERSION}-${TIMESTAMP}-${BUILD_NUMBER}"

mvn versions:set -DnewVersion="${VERSION}"
