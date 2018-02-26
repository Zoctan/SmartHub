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


echo '[*] rm log' &&

rm /tmp/${prefix}* &&

echo '[*] git pull' &&

git pull &&

ps -ef | grep '[.]/manage.py runserver' | awk '{print $2}' | xargs kill -9 ;

echo '[*] kill api' &&

ps -ef | grep '[.]/run_redis_timers.py' | awk '{print $2}' | xargs kill -9 ;

echo '[*] kill redis timers' &&

ps -ef | grep '[.]/run_redis_watt.py' | awk '{print $2}' | xargs kill -9 ;

echo '[*] kill redis watt' &&

#nohup ./manage.py runserver  2>/tmp/${prefix}out 1>/tmp/${prefix}out &
nohup ./manage.py runserver  2>/dev/null 1>/dev/null &

echo '[*] start api' &&

sleep 0.5 &&

nohup ./run_redis_timers.py 2>/tmp/${prefix}timers_err 1>/tmp/${prefix}timers_out &

echo '[*] start redis timers' &&

sleep 0.5 &&

nohup ./run_redis_watt.py 2>/tmp/${prefix}watt_err 1>/tmp/${prefix}watt_out &

echo '[*] start redis timers' &&

sleep 0.5 &&

echo '[*] start check error' &&

check_error /tmp/