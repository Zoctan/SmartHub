#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from time import sleep

import requests
from flask import jsonify, g, request

from app import db
from . import decorators
from ..models import User, Hub, Device

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}

'''
插座本身是开着的，因为需要WIFI控制开关
控制的只是继电器开关，而官网查询只是查询插座状态
def hub_online(onenet_id):
    # https://open.iot.10086.cn/doc/art262.html#68
    url = 'http://api.heclouds.com/devices/'
    cmd_url = url + onenet_id
    # 产品API
    # https://open.iot.10086.cn/product?pid=99569
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.get(cmd_url, headers=headers)
    return response.json()['data']
'''

def hub_online(onenet_id):
    # https://open.iot.10086.cn/doc/art260.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id={}&qos=1&timeout=100&type=0'.format(onenet_id)
    # 由于无法判断继电器本身是否在线，只能通过下方命令判断了
    data = '{match}0'
    response = requests.post(cmd_url, data=data, headers=headers)
    sleep(1)
    query_url = url + '/' + response.json()['data']['cmd_uuid']
    query_response = requests.get(query_url, headers=headers)
    print(query_response.json())
    return query_response.json()['data']['status'] != 0


@decorators.route('/api/hubs', methods=['GET'])
def get_hubs():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not user:
        return jsonify({'msg': 'no', 'error': '请带上token查询'})
    hub_list = []
    for hub in user.hubs:
        online = hub_online(hub.onenet_id)
        tmp = {'online': online}
        tmp.update(hub.to_json())
        hub_list.append(tmp)
    return jsonify({'msg': 'ok', 'result': hub_list})


@decorators.composed(decorators.route('/api/hubs', methods=['POST']), decorators.json_required)
def add_hub():
    onenet_id = request.json.get('onenet_id')
    mac = request.json.get('mac')
    # require these value
    if not onenet_id or not mac:
        return jsonify({'msg': 'no', 'error': '缺少参数: 设备号 MAC地址'})
    if Hub.query.filter_by(onenet_id=onenet_id).first():
        return jsonify({'msg': 'no', 'error': '插座已存在'})
    hub = Hub(user_id=g.current_user.id, mac=mac, onenet_id=onenet_id)
    db.session.add(hub)
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/hubs/<device_id>', methods=['DELETE']))
def delete_hub(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    db.session.delete(hub)
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/hubs/<device_id>', methods=['PUT']), decorators.json_required)
def update_hub(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    hub.name = request.json.get('name')
    return jsonify({'msg': 'ok'})


# https://open.iot.10086.cn/doc/art257.html#68
# 发送命令
def send_order(device_id, order, status):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id={}&qos=1&timeout=100&type=0'.format(device_id)
    data = None
    if order == 'turn':
        # 继电器开关
        data = '{Relay}' + status
    elif order == 'reset':
        # 清除存储的所有用电器特征值数据
        data = '{reset}' + status
    elif order == 'store':
        # 存储当前的用电器的特征
        data = '{store}' + status
    elif order == 'match':
        # 识别当前的用电器，更新list的值
        data = '{match}' + status
    if data is None:
        return '命令错误'
    else:
        response = requests.post(cmd_url, data=data, headers=headers)
        sleep(1)
        query_url = url + '/' + response.json()['data']['cmd_uuid']
        query_response = requests.get(query_url, headers=headers)
        status = query_response.json()['data']['status']
        if status == 0:
            msg = '设备不在线'
        elif status == 1:
            msg = '命令已创建'
        elif status == 2:
            msg = '命令已发往设备'
        elif status == 3:
            msg = '命令发往设备失败'
        elif status == 4:
            msg = '设备正常响应'
        elif status == 5:
            msg = '命令执行超时'
        else:
            msg = '设备响应消息过长'
        return msg


@decorators.route('/api/hubs/<device_id>/order', methods=['GET'])
def hub_order(device_id):
    order = request.args.get('order')
    status = request.args.get('status')
    return jsonify({'msg': send_order(device_id, order, status)})
