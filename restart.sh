#!/usr/bin/env bash

prefix='smarthub_'

function check_error() {
  local files=$1${prefix}'*'
  for file in $(find ${files}); do
    local error=$(grep -i error ${file})
    if [[ ! -z ${error} ]]; then
      echo "[!]$file error"
    fi
  done
}

ps -ef | grep '[.]/manage.py runserver' | awk '{print $2}' | xargs kill -9 &&
ps -ef | grep '[.]/run_redis_timers.py' | awk '{print $2}' | xargs kill -9 &&

rm /tmp/${prefix}* &&

git pull &&
nohup ./manage.py runserver  2>/tmp/${prefix}err 1>/tmp/${prefix}out &
nohup ./run_redis_timers.py 2>/tmp/${prefix}timers_err 1>/tmp/${prefix}timers_out &

check_error /tmp/