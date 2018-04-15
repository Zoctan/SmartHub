#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import g, request
from app import db
from . import decorators
from .result import Result
from ..models import Hub, Device, MonthSpare, HourSpare
from ..tools.onenet import send_order, onenet_header
import requests
import time


def hub_connected(onenet_id):
    # https://open.iot.10086.cn/doc/art262.html#68
    url = 'http://api.heclouds.com/devices/'
    cmd_url = url + onenet_id
    # 产品API
    # https://open.iot.10086.cn/product?pid=99569
    response = requests.get(cmd_url, headers=onenet_header)
    return response.json()['data']['online']


def hub_is_electric(onenet_id):
    # 查询单个数据流
    # https://open.iot.10086.cn/doc/art261.html#68
    url = 'http://api.heclouds.com/devices/{}/datastreams/Relay'.format(onenet_id)
    response = requests.get(url, headers=onenet_header)
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
    return Result.success('成功获取插座信息', hub_list)


@decorators.composed(decorators.route('/api/hubs', methods=['POST']), decorators.json_required)
def add_hub():
    onenet_id = request.json.get('onenet_id')
    mac = request.json.get('mac')
    # require these value
    if not onenet_id or not mac:
        return Result.error('缺少参数: 设备号 MAC地址')
    if Hub.query.filter_by(onenet_id=onenet_id).first():
        return Result.error('插座已被添加')
    hub = Hub(user_id=g.current_user.id, mac=mac, onenet_id=onenet_id)
    db.session.add(hub)
    db.session.flush()
    # 相应的建立月用电和小时用电表
    month = MonthSpare()
    month.hub_id = onenet_id
    month.current_month = int(time.strftime('%m', time.localtime(time.time())))
    db.session.add(month)
    hour = HourSpare()
    hour.hub_id = onenet_id
    db.session.add(hour)
    return Result.success('成功添加插座')


@decorators.composed(decorators.route('/api/hubs', methods=['DELETE']), decorators.json_required)
def delete_hub():
    onenet_id = request.json.get('onenet_id')
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return Result.error('插座不存在')
    db.session.delete(hub)
    return Result.success('成功删除插座')


@decorators.composed(decorators.route('/api/hubs', methods=['PUT']), decorators.json_required)
def update_hub():
    onenet_id = request.json.get('onenet_id')
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return Result.error('插座不存在')
    hub.name = request.json.get('name')
    return Result.success('成功修改插座')


@decorators.composed(decorators.route('/api/hubs/order', methods=['POST']), decorators.json_required)
def hub_order():
    onenet_id = request.json.get('onenet_id')
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return Result.error('插座不存在')
    order = request.json.get('order')
    status = request.json.get('status')
    if order == 'reset':
        # 相应地也要清除所有数据库保存的用电器信息
        devices = Device.query.filter_by(hub_id=onenet_id).all()
        for device in devices:
            db.session.delete(device)
    return Result.success('成功下达指令', send_order(onenet_id, order, status)[0])
