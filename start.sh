#!/usr/bin/env bash

git pull
./manage.py runserver
./redis_timers.py