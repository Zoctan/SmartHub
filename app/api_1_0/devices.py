#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from flask import request
from app import db
from . import decorators
from .hubs import send_order
from ..models import Hub, Device
from .qiniuyun import refresh_cdn
from .result import Result
from ..tools.onenet import onenet_header


@decorators.composed(decorators.route('/api/hubs/devices', methods=['POST']), decorators.json_required)
def add_device():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return Result.error('插座不存在')
    name = request.json.get('name')
    if name is None:
        return Result.error('用电器名不能为空')
    eigenvalue = request.json.get('eigenvalue')
    if eigenvalue == 0:
        return Result.error('特征值不能为0')
    device = Device(eigenvalue=eigenvalue, hub_id=hub_id, name=name)
    db.session.add(device)
    return {'msg': '成功添加用电器', 'error': 0}


@decorators.composed(decorators.route('/api/hubs/devices', methods=['PUT']), decorators.json_required)
def update_device():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return Result.error('插座不存在')
    name = request.json.get('name')
    if name is None:
        return Result.error('用电器名不能为空')
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=hub_id).first()
    device.name = name
    return Result.success('成功修改用电器')


@decorators.composed(decorators.route('/api/hubs/devices/img', methods=['PUT']), decorators.json_required)
def update_device_img():
    hub_id = request.json.get('hub_id')
    hub = Hub.query.filter_by(onenet_id=hub_id).first()
    if not hub:
        return Result.error('插座不存在')
    img = request.json.get('img')
    if not img or img == '':
        return Result.error('图片链接不能为空')
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=hub_id).first()
    device.img = img
    refresh_cdn([device.img])
    return Result.success('成功修改用电器图片')


@decorators.route('/api/hubs/devices/<onenet_id>', methods=['GET'])
def get_device(onenet_id):
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return Result.error('插座不存在')
    # 先尝试识别当前用电器
    send_order(onenet_id, 'match', 1, 6)
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(onenet_id)
    response = requests.get(url, headers=onenet_header)
    # 识别的用电器的特征值
    eigenvalue = response.json()['data']['current_value']
    # 100: 空载
    if eigenvalue == 100:
        return Result.error('空载')
    # 0：无效或不能识别当前用电器
    if eigenvalue == 0:
        # 提示用户进行用电器添加
        return Result.error('无法确认当前用电器，请先手动添加')
    else:
        # 先从数据库找
        device = Device.query.filter_by(eigenvalue=eigenvalue, hub_id=onenet_id).first()
        if device:
            return Result.success('成功获取当前设备', [device.to_json()])
        else:
            # 提示用户进行用电器添加
            return Result.error('无法确认当前用电器，请先手动添加')
