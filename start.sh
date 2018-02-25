#!/usr/bin/env bash

git pull
nohup ./manage.py runserver &
nohup ./redis_timers.py &