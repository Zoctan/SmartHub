#!/usr/bin/env bash

git pull
nohup ./manage.py runserver  2>/tmp/smarthub_err 1>/tmp/smarthub_out &
nohup ./run_redis_timers.py 2>/tmp/smarthub_timers_err 1>/tmp/smarthub_timers_out &