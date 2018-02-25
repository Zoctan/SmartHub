#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify

from app import db
from app.models import Timer, Hub
from . import decorators
from .redis_timers import RedisTimer


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
    db.session.flush()
    # 加到redis
    RedisTimer(id=timer.id, hub_id=device_id, repeat=repeat, time=time, power=power, status=1).set()
    return jsonify({'msg': 'ok', 'result': '定时器添加成功'})


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
    timer = Timer.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    if not timer:
        return jsonify({'msg': 'no', 'error': '定时器不存在'})
    name = request.json.get('name')
    power = request.json.get('power')
    if name == '':
        if power == 0:
            name = '定时关机'
        else:
            name = '定时开机'
    timer.name = name
    timer.power = power
    timer.repeat = request.json.get('repeat')
    timer.time = request.json.get('time')
    timer.status = request.json.get('status')
    RedisTimer(id=timer.id, hub_id=device_id, repeat=timer.repeat, time=timer.time, power=timer.power,
            status=timer.status).update()
    return jsonify({'msg': 'ok', 'result': '定时器修改成功'})


@decorators.composed(decorators.route('/api/hubs/timers/<device_id>', methods=['DELETE']), decorators.json_required)
def delete_timer(device_id):
    timer = Timer.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    if not timer:
        return jsonify({'msg': 'no', 'error': '定时器不存在'})
    RedisTimer(id=timer.id, hub_id=timer.hub_id).delete()
    db.session.delete(timer)
    return jsonify({'msg': 'ok', 'result': '定时器删除成功'})
