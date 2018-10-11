
# calculate

MVN_VERSION="$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)"
MAJOR_VERSION="$(echo ${MVN_VERSION} | head -c 1)"
MINOR_VERSION="$(echo ${MVN_VERSION}| head -c 3 | tail -c 1)"


git checkout -b "release-${MAJOR_VERSION}-${MINOR_VERSION}"
git push "release-${MAJOR_VERSION}-${MINOR_VERSION}"
git checkout master


mvn versions:set -DnewVersion="${MAJOR_VERSION}.$(( MINOR_VERSION + 1 )).0-SNAPSHOT"
mvn clean deploy -Pimage -Ddocker.registry.host=gcr.io
git add -A
git commit -m "Release commit"
