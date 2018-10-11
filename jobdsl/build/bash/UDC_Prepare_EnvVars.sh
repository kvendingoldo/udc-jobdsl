if [[ "${GIT_BRANCH}" =~ release ]]; then
  VERSION="${VERSION}-rtm"
fi
