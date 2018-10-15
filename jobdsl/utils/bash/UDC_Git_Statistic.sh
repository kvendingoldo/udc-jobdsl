#!/usr/bin/env bash

date --date='week ago' "+%A, %B %d, %Y" > date_start.txt
date "+%A, %B %d, %Y" > date_finish.txt

git log --after="$(date --date='week ago' '+%D')" --before="$(date '+%D')" --pretty=format:"%aN %aE %cr %h %s" | sort > git_statistics.txt

cat git_statistics.txt