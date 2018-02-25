#!/usr/bin/env bash

function check_error() {
  local files=$1'smarthub_*'
  for file in `find $files `; do
    local error=`grep -i error $file`
    if [[ ! -z $error ]]; then
      echo "[!]$file error"
    fi
  done
}

ps -ef | grep '[.]/manage.py runserver' | awk '{print $2}' | xargs kill -9
ps -ef | grep '[.]/run_redis_timers.py' | awk '{print $2}' | xargs kill -9

rm /tmp/smarthub_*

git pull
nohup ./manage.py runserver  2>/tmp/smarthub_err 1>/tmp/smarthub_out &
nohup ./run_redis_timers.py 2>/tmp/smarthub_timers_err 1>/tmp/smarthub_timers_out &

check_error /tmp/