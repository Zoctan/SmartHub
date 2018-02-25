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

git pull &&

echo '[*] git pull' &&

ps -ef | grep '[.]/manage.py runserver' | awk '{print $2}' | xargs kill -9 ;

echo '[*] kill api' &&

ps -ef | grep '[.]/run_redis_timers.py' | awk '{print $2}' | xargs kill -9 ;

echo '[*] kill redis timers'

rm /tmp/${prefix}* &&

echo '[*] rm log' &&

nohup ./manage.py runserver  2>/tmp/${prefix}err 1>/tmp/${prefix}out &

echo '[*] start api' &&

sleep 0.5 &&

nohup ./run_redis_timers.py 2>/tmp/${prefix}timers_err 1>/tmp/${prefix}timers_out &

echo '[*] start redis timers' &&

sleep 0.5 &&

echo '[*] start check error' &&

check_error /tmp/