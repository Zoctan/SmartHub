#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Blueprint

smart = Blueprint('smart_1_0', __name__)

from . import index, errors, api, onenet, devices, crontabs
