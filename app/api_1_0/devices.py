#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from flask import jsonify, request
from time import sleep
from app import db
from . import decorators
from .hubs import send_order
from ..models import Hub, Device

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}


@decorators.composed(decorators.route('/api/hubs/device/<device_id>', methods=['POST']), decorators.json_required)
def add_device(device_id):
    name = request.json.get('name')
    if name is None:
        return jsonify({'msg': 'no', 'error': '用电器名不能为空'})
    # 先尝试识别当前用电器，避免用户重复添加
    send_order(device_id, 'match', 1)
    sleep(1)
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
    response = requests.get(url, headers=headers)
    # 识别的用电器的特征值
    eigenvalue = response.json()['data']['current_value']
    if eigenvalue != 0:
        # 有可能上次保存了特征值，但是没识别成功
        device = Device.query.filter_by(eigenvalue=eigenvalue, hub_id=device_id).first()
        if device is None:
            device = Device(eigenvalue=eigenvalue, hub_id=device_id, name=name)
            db.session.add(device)
            return jsonify({'msg': 'ok', 'result': '用电器添加成功'})
        else:
            return jsonify({'msg': 'no', 'result': '请勿重复添加相同的用电器'})
    else:
        # 如果仍为0：无效或不能识别当前用电器
        # 保存该用电器，先让插座Flash存下来，再本地数据库存
        msg, query_url = send_order(device_id, 'store', 1)
        if msg != '设备正常响应':
            sleep(1)
            query_response = requests.get(query_url, headers=headers)
            status = query_response.json()['data']['status']
            if status != 4:
                return jsonify({'msg': 'no', 'result': '插座Flash可能未保存成功'})
        # match更新list
        msg, query_url = send_order(device_id, 'match', 1)
        if msg != '设备正常响应':
            sleep(1)
            query_response = requests.get(query_url, headers=headers)
            status = query_response.json()['data']['status']
            if status != 4:
                return jsonify({'msg': 'no', 'result': '识别失败，请重新添加用电器'})
        url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
        response = requests.get(url, headers=headers)
        eigenvalue = response.json()['data']['current_value']
        device = Device(eigenvalue=eigenvalue, hub_id=device_id, name=name)
        db.session.add(device)
        return jsonify({'msg': 'ok', 'result': '用电器添加成功'})


@decorators.composed(decorators.route('/api/hubs/device/<device_id>', methods=['PUT']), decorators.json_required)
def update_device(device_id):
    name = request.json.get('name')
    if name is None:
        return jsonify({'msg': 'no', 'error': '用电器名不能为空'})
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    device.name = name
    return jsonify({'msg': 'ok', 'result': '用电器修改成功'})


@decorators.composed(decorators.route('/api/hubs/device/img/<device_id>', methods=['PUT']), decorators.json_required)
def update_device_img(device_id):
    img = request.json.get('img')
    if not img or img == '':
        return jsonify({'msg': 'no', 'error': '图片链接不能为空'})
    device = Device.query.filter_by(id=request.json.get('id'), hub_id=device_id).first()
    device.img = request.json.get('img')
    return jsonify({'msg': 'ok', 'result': '用电器图片修改成功'})


@decorators.route('/api/hubs/device/<device_id>', methods=['GET'])
def get_device(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    # 识别的用电器的特征值
    url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
    response = requests.get(url, headers=headers)
    eigenvalue = response.json()['data']['current_value']
    # 0：无效或不能识别当前用电器
    if eigenvalue != 0:
        # 先从数据库找
        device = Device.query.filter_by(eigenvalue=eigenvalue).first()
        if device:
            return jsonify({'msg': 'ok', 'result': device.to_json()})
    # 提示用户进行用电器添加
    return jsonify({'msg': 'no', 'error': '无法确认当前用电器，请手动添加该用电器'})
