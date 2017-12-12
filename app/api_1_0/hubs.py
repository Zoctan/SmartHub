#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import jsonify, g
from . import decorators
from ..models import User
import requests
from time import sleep


# test_init
# db.drop_all();db.create_all();user = models.User();user.username = 'test';user.password = 'test';db.session.add(user);hub = models.Hub();hub.name = '智能可识别插座测试样品';hub.mac = 'AB:CD:EF:GH:IJ:KL';hub.user_id = 1;hub.onenet_id='19959358';db.session.add(hub);db.session.commit()
def hub_online(onenet_id):
    # https://open.iot.10086.cn/doc/art262.html#68
    url = 'http://api.heclouds.com/devices/'
    cmd_url = url + onenet_id
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.get(cmd_url, headers=headers)
    return response.json()['data']


@decorators.route('/api/hubs', methods=['GET'])
def get_hubs():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not user:
        return jsonify({'msg': 'no', 'error': 'user doesn\'t exist'})
    hub_list = []
    for hub in user.hubs:
        online = hub_online(hub.onenet_id)['online']
        tmp = {'online': online}
        tmp.update(hub.to_json())
        hub_list.append(tmp)
    return jsonify({'msg': 'ok', 'result': hub_list})


@decorators.route('/api/hubs/<status>/<device_id>', methods=['GET'])
def hub_turn_on_or_off(status, device_id):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id={}&qos=1&timeout=100&type=0'.format(device_id)
    data = '{xx}' + status
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.post(cmd_url, data=data, headers=headers)

    query_url = url + '/' + response.json()['data']['cmd_uuid']
    query_response = requests.get(query_url, headers=headers)

    cmd_res_url = query_url + '/resp'
    cmd_response = requests.get(cmd_res_url, headers=headers)
    sleep(1)
    return jsonify({'cmd_status': query_response.json()['data']['desc'],
                    'text': cmd_response.text})
