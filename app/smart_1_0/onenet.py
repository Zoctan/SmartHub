#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import request, jsonify
from . import smart
import base64
import hashlib
import requests
import pickle
import time


@smart.route('/api/va/<which>', methods=['GET'])
def v_a(which):
    if which == 'v':
        with open('/tmp/onenet_v', 'rb') as f:
            now_data_dict = pickle.load(f)
    else:
        with open('/tmp/onenet_a', 'rb') as f:
            now_data_dict = pickle.load(f)
    return jsonify({'key': now_data_dict['msg']['ds_id'],
                    'value': now_data_dict['msg']['value']})


@smart.route('/onenet', methods=['GET', 'POST'])
def onenet():
    if request.method == 'GET':
        nonce = request.args.get('nonce')
        signature = request.args.get('signature')
        msg = request.args.get('msg')
        if nonce and signature and msg:
            tmp = hashlib.md5(('752481828' + nonce + msg).encode())
            if base64.b64encode(tmp.digest()).decode() == signature:
                return msg
    """
    # calculate eigenvalue by python and get current device image and name
    if request.method == 'POST':
        file = None
        if request.json['msg']['ds_id'] == 'V':  # !every 24 hour to -> day
            file = '/tmp/onenet_V_{}'.format(request.json['msg']['dev_id'])
        elif request.json['msg']['ds_id'] == 'A':
            file = '/tmp/onenet_A_{}'.format(request.json['msg']['dev_id'])
        elif request.json['msg']['ds_id'] == 'W':
            file = '/tmp/onenet_W_{}'.format(request.json['msg']['dev_id'])
        elif request.json['msg']['ds_id'] == 'Q':
            file = '/tmp/onenet_Q_{}'.format(request.json['msg']['dev_id'])
        with open(file, 'rb') as f:
            now_data_dict = pickle.load(f)
        with open(file, 'wb') as f:
            pickle.dump(request.json, f)
        return 'get'
    """
    return 'error'


@smart.route('/test/<which>', methods=['GET'])
def test_onenet(which):
    # {"msg":{"at":1508948943731,"type":1,"ds_id":"Humidity","value":0,"dev_id":19959358},"msg_signature":"i20/b74tIJvqMFek6q9Ffw==","nonce":"?@H&9p9E"}
    if which == 'v':
        with open('/tmp/onenet_v', 'rb') as f:
            now_data = pickle.load(f)
    else:
        with open('/tmp/onenet_a', 'rb') as f:
            now_data = pickle.load(f)
    return now_data


@smart.route('/led/<color>/<status>', methods=['GET'])
def led_turn_on_or_off(color, status):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id=19959358&qos=1&timeout=0&type=0'
    data = '{' + color + 'led}' + status
    headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}
    response = requests.post(cmd_url, data=data, headers=headers)
    query_url = url + '/' + response.json()['data']['cmd_uuid']
    time.sleep(1)
    query_response = requests.get(query_url, headers=headers)
    cmd_res_url = query_url + '/resp'
    cmd_response = requests.get(cmd_res_url, headers=headers)
    return query_response.json()['data']['desc'] + '<br>' + cmd_response.text