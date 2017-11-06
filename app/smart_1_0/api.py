#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import jsonify, request
from . import smart
from .common import is_found
from ..models import *
import pickle
import requests
import time
import re


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
    time.sleep(0.5)
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


def process_crontab(key_word, task):
    crontab = '/etc/crontab'
    if is_found(crontab, key_word):
        if not is_found(crontab, task):
            # replace crontab
            with open(crontab, 'w') as f:
                lines = f.readlines()
                for i in range(len(lines)):
                    if re.findall(key_word, lines[i]):
                        lines[i] = task
                        f.writelines(lines)
                        break
    else:
        if task:
            # write crontab
            with open(crontab, 'w') as f:
                f.write(task)


@smart.route('/crontabs', methods=['POST'])
def set_crontabs():
    power_on = request.json.get('on')
    power_off = request.json.get('off')
    print(power_on)
    if power_on[0] is None:
        power_on = None
    else:
        minute = power_on[1]
        hour = power_on[0]
        day = ''
        month = ''
        week = ''
        user = 'root'
        command = 'curl smart.txdna.cn/hub/on'
        power_on = '{} {}	{} {} {}	{}    {}'.format(
                minute, hour, day, month, week, user, command)
    time.sleep(0.5)
    #process_crontab('/hub/on', power_on)
    return jsonify({'msg': 'ok', 'result': 'ok'})
