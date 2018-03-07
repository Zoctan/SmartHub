#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from flask import jsonify, request
from app import db
from . import decorators
from .hubs import send_order
from ..models import Hub, Device
from .qiniuyun import refresh_cdn

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}


@decorators.composed(decorators.route('/api/hubs/devices/<device_id>', methods=['POST']), decorators.json_required)
def add_device(device_id):
    name = request.json.get('name')
    if name is None:
        return jsonify({'msg': 'no', 'error': '用电器名不能为空'})
    eigenvalue = request.json.get('eigenvalue')
    if eigenvalue == 0:
        return jsonify({'msg': 'no', 'error': '特征值不能为0'})
    device = Device(eigenvalue=eigenvalue, hub_id=device_id, name=name)
    db.session.add(device)
    return jsonify({'msg': 'ok', 'result': '用电器添加成功'})


@decorators.composed(decorators.route('/api/hubs/devices/<device_id>', methods=['PUT']), decorators.json_required)
def update_device(device_id):
    name = request.json.get('name')
    if name is None:
        return jsonify({'msg': 'no', 'error': '用电器名不能为空'})
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    device.name = name
    return jsonify({'msg': 'ok', 'result': '用电器修改成功'})


@decorators.composed(decorators.route('/api/hubs/devices/img/<device_id>', methods=['PUT']), decorators.json_required)
def update_device_img(device_id):
    img = request.json.get('img')
    if not img or img == '':
        return jsonify({'msg': 'no', 'error': '图片链接不能为空'})
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    device.img = request.json.get('img')
    refresh_cdn([device.img])
    return jsonify({'msg': 'ok', 'result': '用电器图片修改成功'})


@decorators.route('/api/hubs/devices/<device_id>', methods=['GET'])
def get_device(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    # 先尝试识别当前用电器
    send_order(device_id, 'match', 1, 6)
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
    response = requests.get(url, headers=headers)
    # 识别的用电器的特征值
    eigenvalue = response.json()['data']['current_value']
    # 100: 空载
    if eigenvalue == 100:
        return jsonify({'msg': 'no', 'error': '空载'})
    # 0：无效或不能识别当前用电器
    if eigenvalue == 0:
        # 提示用户进行用电器添加
        return jsonify({'msg': 'no', 'error': '无法确认当前用电器，请手动添加该用电器'})
    else:
        # 先从数据库找
        device = Device.query.filter_by(eigenvalue=eigenvalue, hub_id=device_id).first()
        if device:
            return jsonify({'msg': 'ok', 'result': device.to_json()})
        else:
            # 提示用户进行用电器添加
            return jsonify({'msg': 'no', 'error': '无法确认当前用电器，请手动添加该用电器'})
