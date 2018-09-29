set -xe
IMAGE='gcr.io/university-course/udc-backend-service'
DELETE_BEFORE="$(date +'%Y-%m-%d' --date='-7 day')"

LIST_OF_OLD_IMAGES=$(gcloud container images list-tags \
                     ${IMAGE} \
                     --limit=999999 \
                     --sort-by=TIMESTAMP \
                     --filter="timestamp.datetime < '${DELETE_BEFORE}'" \
                     --format='get(digest)'
                     )

for digest in ${LIST_OF_OLD_IMAGES}; do
    gcloud container images delete -q --force-delete-tags "${IMAGE}@${digest}"
done
