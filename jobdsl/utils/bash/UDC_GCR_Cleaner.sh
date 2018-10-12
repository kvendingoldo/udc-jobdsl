set -xe

REPOSITORIES=$(gcloud container images list \
               --repository=gcr.io/university-course/udc/dev \
               --format='get(name)')

DELETE_BEFORE="$(date +'%Y-%m-%d' --date='-7 day')"

for REPOSITORY in ${REPOSITORIES}; do
    LIST_OF_OLD_IMAGES=$(gcloud container images list-tags \
                     ${REPOSITORY}/udc-backend-service \
                     --limit=999999 \
                     --sort-by=TIMESTAMP \
                     --filter="timestamp.datetime < '${DELETE_BEFORE}'" \
                     --format='get(digest)')
    for DIGEST in ${LIST_OF_OLD_IMAGES}; do
        gcloud container images delete -q --force-delete-tags "${REPOSITORY}/udc-backend-service@${DIGEST}"
    done
done