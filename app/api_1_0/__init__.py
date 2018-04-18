#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Blueprint

api = Blueprint('api_1_0', __name__)

from . import result, authentication, users, errors, hubs, timers, devices, qiniuyun, onenet, spares, mail
