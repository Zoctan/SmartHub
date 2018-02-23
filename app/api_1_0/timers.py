#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify

from app import db
from app.models import Timer, Hub
from . import decorators


@decorators.composed(decorators.route('/api/hubs/timers/<device_id>', methods=['POST']), decorators.json_required)
def add_timer(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    name = request.json.get('name')
    power = request.json.get('power')
    repeat = request.json.get('repeat')
    time = request.json.get('time')
    if name == '':
        if power == 0:
            name = '定时关机'
        else:
            name = '定时开机'
    timer = Timer(hub_id=device_id, name=name, power=power, repeat=repeat, time=time, status=1)
    db.session.add(timer)
    return jsonify({'msg': 'ok'})


@decorators.route('/api/hubs/timers/<device_id>', methods=['GET'])
def hub_all_timers(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    timer_list = []
    for timer in hub.timers:
        timer_list.append(timer.to_json())
    return jsonify({'msg': 'ok', 'result': timer_list})


@decorators.composed(decorators.route('/api/hubs/timers/<device_id>', methods=['PUT']), decorators.json_required)
def update_timer(device_id):
    id = request.json.get('id')
    timer = Timer.query.filter_by(id=id, hub_id=device_id).first()
    if not timer:
        return jsonify({'msg': 'no', 'error': '定时器不存在'})
    name = request.json.get('name')
    power = request.json.get('power')
    if name == '':
        if power == 0:
            name = '定时关机'
        else:
            name = '定时开机'
    timer.name, = name
    timer.power = power
    timer.repeat = request.json.get('repeat')
    timer.time = request.json.get('time')
    timer.status = request.json.get('status')
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/hubs/timers/<device_id>', methods=['DELETE']), decorators.json_required)
def delete_timer(device_id):
    id = request.json.get('id')
    timer = Timer.query.filter_by(id=id, hub_id=device_id).first()
    if not timer:
        return jsonify({'msg': 'no', 'error': '定时器不存在'})
    db.session.delete(timer)
    return jsonify({'msg': 'ok'})
