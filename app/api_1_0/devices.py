#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from flask import jsonify, request

from app import db
from . import decorators
from .hubs import send_order
from ..models import Hub, Device

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}


@decorators.composed(decorators.route('/api/hubs/device/<device_id>', methods=['POST']), decorators.json_required)
def add_device(device_id):
    name = request.json.get('name')
    if name is None:
        return jsonify({'msg': 'no', 'error': '用电器备注不能为空'})
    # 如果数据库没有，即还没保存该用电器，先让插座flash存下来，再本地数据库存
    send_order(device_id, 'store', 1)
    # match更新list
    send_order(device_id, 'match', 1)
    # 识别的用电器的特征值
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
    response = requests.get(url, headers=headers)
    device = Device(eigenvalue=response.json()['data']['current_value'], onenet_id=device_id, name=name)
    db.session.add(device)
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/hubs/device/<device_id>', methods=['PUT']), decorators.json_required)
def update_device_img(device_id):
    oldname = request.json.get('oldname')
    if oldname is None:
        return jsonify({'msg': 'no', 'error': '用电器备注不能为空'})
    device = Device.query.filter_by(name=oldname, onenet_id=device_id).first()
    device.img = request.json.get('img')
    device.name = request.json.get('name')
    return jsonify({'msg': 'ok'})


@decorators.route('/api/hubs/device/<device_id>', methods=['GET'])
def get_device(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    # 识别的用电器的特征值
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
    response = requests.get(url, headers=headers)
    eigenvalue = response.json()['data']['current_value']
    if eigenvalue != 0:
        # 先从数据库找
        device = Device.query.filter_by(eigenvalue=eigenvalue).first()
        if device:
            return jsonify({'msg': 'ok', 'result': device.to_json()})
    # 提示用户进行用电器添加
    return jsonify({'msg': 'no', 'error': '无法确认当前用电器'})
