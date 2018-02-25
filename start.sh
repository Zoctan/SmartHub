#!/usr/bin/env bash

git pull
nohup ./manage.py runserver &
nohup ./app/redis_timers.py &