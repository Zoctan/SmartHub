#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template, request, jsonify
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


@smart.route('/devices/name/<old_name>', methods=['POST'])
def modify_device_name(old_name):
    name = request.json.get('name')
    device = Device.query.filter_by(name=old_name).first()
    device.name = name
    try:
        db.session.commit()
        return jsonify({'msg': 'ok', 'result': device.to_json()})
    except Exception as e:
        db.session.rollback()
        return jsonify({'msg': 'no', 'error': str(e)})
    finally:
        db.session.remove()
