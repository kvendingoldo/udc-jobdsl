CORPORATIVE_EMAIL_DOMAIN='yandex.ru'

if [[ "$(echo ${GIT_AUTHOR_EMAIL} | awk -F'@' '{ print $2 }')" != "${CORPORATIVE_EMAIL_DOMAIN}" ]]; then
		echo 'ERROR: Invalid Git Author E-Mail'
    exit 2
fi

if [[ "$(echo ${GIT_COMMITTER_EMAIL} | awk -F'@' '{ print $2 }')" != "${CORPORATIVE_EMAIL_DOMAIN}" ]]; then
		echo 'ERROR: Invalid Commiter E-Mail'
    exit 2
fi

if [[ "$(git show --format="%aE" --quiet -s HEAD | awk -F'@' '{ print $2 }')" != "${CORPORATIVE_EMAIL_DOMAIN}" ]]; then
		echo 'ERROR: Invalid Author E-Mail'
    exit 2
fi
