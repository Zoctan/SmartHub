#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import jsonify, request
from . import smart
import pickle
import json
import requests
import time
from ..models import *


@smart.route('/api/va/<which>', methods=['GET'])
def v_a(which):
    if which == 'v':
        with open('/tmp/onenet_v', 'rb') as f:
            now_data_dict = json.loads(pickle.load(f).decode())
    else:
        with open('/tmp/onenet_a', 'rb') as f:
            now_data_dict = json.loads(pickle.load(f).decode())
    return jsonify({'key': now_data_dict['msg']['ds_id'],
                    'value': now_data_dict['msg']['value']})


@smart.route('/hub/online', methods=['GET'])
def hub_online():
    # https://open.iot.10086.cn/doc/art262.html#68
    url = 'http://api.heclouds.com/devices/'
    cmd_url = url + '19959358'
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.get(cmd_url, headers=headers)
    return jsonify({'id': response.json()['data']['id'],
                    'protocol': response.json()['data']['protocol'],
                    'online': response.json()['data']['online']})


@smart.route('/hub/<status>', methods=['GET'])
def hub_turn_on_or_off(status):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id=19959358&qos=1&timeout=100&type=0'
    data = '{xx}' + status
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.post(cmd_url, data=data, headers=headers)

    query_url = url + '/' + response.json()['data']['cmd_uuid']
    time.sleep(1)
    query_response = requests.get(query_url, headers=headers)

    cmd_res_url = query_url + '/resp'
    cmd_response = requests.get(cmd_res_url, headers=headers)
    return jsonify({'cmd_status': query_response.json()['data']['desc'],
                    'text': cmd_response.text})


@smart.route('/devices/name/<old_name>', methods=['POST'])
def modify_device_name(old_name):
    name = request.json.get('name')
    device = Device.query.filter_by(name=old_name).first()
    device.name = name
    try:
        db.session.commit()
        return jsonify({'msg': 'ok', 'result': device.to_json()})
    except Exception as e:
        db.session.rollback()
        return jsonify({'msg': 'no', 'error': str(e)})
    finally:
        db.session.remove()