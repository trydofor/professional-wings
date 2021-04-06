#!/bin/sh
#
# 统计提交行数
echo 'name       '$(git config --get user.name)
echo 'email      '$(git config --get user.email)
echo 'remote url '$(git config --get remote.origin.url)
echo 'branch     '$(git symbolic-ref --short -q HEAD)
echo '\033[2m┌──────────┬─────────────┬───────────────┬─────────────┐\033[0m'
echo '\033[2m|\033[0m Time     \033[2m|\033[0m Added lines \033[2m|\033[0m Removed lines \033[2m|\033[0m Total lines \033[2m|\033[0m'
git log --author="$(git config --get user.name)" --since=1.day --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "\033[2m|\033[0m 1 day    \033[2m|\033[0m \033[32m%11s\033[0m \033[2m|\033[0m \033[31m%13s\033[0m \033[2m|\033[0m \033[34m%11s\033[0m \033[2m|\033[0m\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=3.day --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "\033[2m|\033[0m 3 days   \033[2m|\033[0m \033[32m%11s\033[0m \033[2m|\033[0m \033[31m%13s\033[0m \033[2m|\033[0m \033[34m%11s\033[0m \033[2m|\033[0m\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=1.week --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "\033[2m|\033[0m 1 week   \033[2m|\033[0m \033[32m%11s\033[0m \033[2m|\033[0m \033[31m%13s\033[0m \033[2m|\033[0m \033[34m%11s\033[0m \033[2m|\033[0m\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=1.month --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "\033[2m|\033[0m 1 month  \033[2m|\033[0m \033[32m%11s\033[0m \033[2m|\033[0m \033[31m%13s\033[0m \033[2m|\033[0m \033[34m%11s\033[0m \033[2m|\033[0m\n", add, subs, loc }' -
echo '\033[2m└──────────┴─────────────┴───────────────┴─────────────┘\033[0m'
