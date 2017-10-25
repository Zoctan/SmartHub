#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import request, render_template
from . import smart
from ..models import *
import base64
import hashlib
import requests
import pickle
import time


@smart.errorhandler(404)
def error_404(error):
    return render_template('error.html')


@smart.route('/', methods=['GET'])
def index():
    return render_template('index.html')


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
    if request.method == 'POST':
        with open('/tmp/onenet', 'wb') as f:
            pickle.dump(request.data, f)
        return 'well'
    return 'error'


@smart.route('/test', methods=['GET'])
def test_onenet():
    # devices = Device.query()
    with open('/tmp/onenet', 'rb') as f:
        now_data = pickle.load(f)
    return now_data


@smart.route('/devices', methods=['GET'])
def get_all_infos():
    return render_template('devices.html')


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
