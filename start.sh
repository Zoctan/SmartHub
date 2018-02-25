#!/usr/bin/env bash

git pull
nohup ./manage.py runserver  2>/tmp/smarthub_err 1>/tmp/smarthub_out &
nohup ./redis_timers.py 2>/tmp/redis_timers_err 1>/tmp/redis_timers_out &