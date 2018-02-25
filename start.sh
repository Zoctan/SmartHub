#!/usr/bin/env bash

git pull
nohup ./manage.py runserver &
nohup ./app/api_1_0/redis_timers.py &