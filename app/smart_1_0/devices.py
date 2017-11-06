#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart
from ..models import *


@smart.route('/devices', methods=['GET'])
def get_all_devices():
    devices = Device.query.all()
    return render_template('devices.html', id='devices', devices=devices)


@smart.route('/devices/<name>', methods=['GET'])
def get_device_info(name):
    device = Device.query.filter_by(name=name).first()
    return render_template('devices.html', id='devices', device=device.to_json())
