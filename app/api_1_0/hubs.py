#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import requests
from flask import jsonify, g, request

from app import db
from . import decorators
from ..models import Hub, Device
from ..tools.onenet import send_order

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}


def hub_connected(onenet_id):
    # https://open.iot.10086.cn/doc/art262.html#68
    url = 'http://api.heclouds.com/devices/'
    cmd_url = url + onenet_id
    # 产品API
    # https://open.iot.10086.cn/product?pid=99569
    response = requests.get(cmd_url, headers=headers)
    return response.json()['data']['online']


def hub_is_electric(onenet_id):
    # 查询单个数据流
    # https://open.iot.10086.cn/doc/art261.html#68
    url = 'http://api.heclouds.com/devices/{}/datastreams/Relay'.format(onenet_id)
    response = requests.get(url, headers=headers)
    # 服务器api查询的是最后一次通电时的状态，插座本身不在线的话该数据就是不对的
    return response.json()['data']['current_value'] == 1


@decorators.route('/api/hubs', methods=['GET'])
def get_hubs():
    hub_list = []
    for hub in g.current_user.hubs:
        # 插座是否在线，插座不在线继电器即无法发送开关命令
        connected = hub_connected(hub.onenet_id)
        # 继电器通电情况
        is_electric = False
        if connected:
            is_electric = hub_is_electric(hub.onenet_id)
        tmp = {'connected': connected, 'is_electric': is_electric}
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
    return jsonify({'msg': 'ok', 'result': '插座添加成功'})


@decorators.route('/api/hubs/<device_id>', methods=['DELETE'])
def delete_hub(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    db.session.delete(hub)
    return jsonify({'msg': 'ok', 'result': '插座删除成功'})


@decorators.composed(decorators.route('/api/hubs/<device_id>', methods=['PUT']), decorators.json_required)
def update_hub(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    hub.name = request.json.get('name')
    return jsonify({'msg': 'ok', 'result': '插座修改成功'})


@decorators.route('/api/hubs/<device_id>/order', methods=['GET'])
def hub_order(device_id):
    order = request.args.get('order')
    status = request.args.get('status')
    if order == 'reset':
        # 相应地也要清除所有数据库保存的用电器信息
        devices = Device.query.filter_by(hub_id=device_id).all()
        for device in devices:
            db.session.delete(device)
    return jsonify({'msg': send_order(device_id, order, status)})
