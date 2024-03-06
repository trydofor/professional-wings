#!/bin/bash -e
THIS_VERSION=2021-12-21
echo -e "\033[37;42;1mScript-Version $THIS_VERSION \033[0m"

# Statistics the commit lines
echo "name   : $(git config --get user.name)"
echo "email  : $(git config --get user.email)"
echo "remote : $(git config --get remote.origin.url)"
echo "branch : $(git symbolic-ref --short -q HEAD)"
echo -e '┌──────────┬─────────────┬───────────────┬─────────────┐'
echo -e '|Time      | Added lines | Removed lines | Total lines |'
git log --author="$(git config --get user.name)" --since=1.day   --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "| 1 day    | \033[32m%11s\033[0m | \033[31m%13s\033[0m | \033[34m%11s\033[0m |\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=3.day   --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "| 3 days   | \033[32m%11s\033[0m | \033[31m%13s\033[0m | \033[34m%11s\033[0m |\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=1.week  --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "| 1 week   | \033[32m%11s\033[0m | \033[31m%13s\033[0m | \033[34m%11s\033[0m |\n", add, subs, loc }' -
git log --author="$(git config --get user.name)" --since=1.month --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 - $2 } END { printf "| 1 month  | \033[32m%11s\033[0m | \033[31m%13s\033[0m | \033[34m%11s\033[0m |\n", add, subs, loc }' -
echo -e '└──────────┴─────────────┴───────────────┴─────────────┘'
