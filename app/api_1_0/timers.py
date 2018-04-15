#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify

from app import db
from app.models import Timer, Hub
from . import decorators
from ..tools.redis_timers import RedisTimer


@decorators.composed(decorators.route('/api/hubs/timers', methods=['POST']), decorators.json_required)
def add_timer():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return jsonify({'msg': '插座不存在', 'error': 1})
    name = request.json.get('name')
    power = request.json.get('power')
    repeat = request.json.get('repeat')
    time = request.json.get('time')
    if name == '':
        if power == 0:
            name = '定时关机'
        else:
            name = '定时开机'
    timer = Timer(hub_id=hub_id, name=name, power=power, repeat=repeat, time=time, status=1)
    db.session.add(timer)
    db.session.flush()
    # 加到redis
    RedisTimer(id=timer.id, hub_id=hub_id, repeat=repeat, time=time, power=power, status=1).set()
    return jsonify({'msg': '定时器添加成功', 'error': 0})


@decorators.route('/api/hubs/timers/<onenet_id>', methods=['GET'])
def hub_all_timers(onenet_id):
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return jsonify({'msg': '插座不存在', 'error': 1})
    timer_list = []
    for timer in hub.timers:
        timer_list.append(timer.to_json())
    return jsonify({'msg': '成功获取所有插座信息', 'error': 0, 'result': timer_list})


@decorators.composed(decorators.route('/api/hubs/timers', methods=['PUT']), decorators.json_required)
def update_timer():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return jsonify({'msg': '插座不存在', 'error': 1})
    timer = Timer.query.filter_by(id=request.json.get('id'), hub_id=hub_id).first()
    if not timer:
        return jsonify({'msg': '定时器不存在', 'error': 1})
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
    if timer.status == 0:
        RedisTimer(id=timer.id, hub_id=timer.hub_id).delete()
    else:
        RedisTimer(id=timer.id, hub_id=hub_id, repeat=timer.repeat,
                time=timer.time, power=timer.power, status=timer.status).update()
    return jsonify({'msg': '定时器修改成功', 'error': 0})


@decorators.composed(decorators.route('/api/hubs/timers', methods=['DELETE']), decorators.json_required)
def delete_timer():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return jsonify({'msg': '插座不存在', 'error': 1})
    timer = Timer.query.filter_by(id=request.json.get('id'), hub_id=hub_id).first()
    if not timer:
        return jsonify({'msg': '定时器不存在', 'error': 1})
    RedisTimer(id=timer.id, hub_id=timer.hub_id).delete()
    db.session.delete(timer)
    return jsonify({'msg': '定时器删除成功', 'error': 0})
