#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import request, jsonify, g
from . import decorators
from ..models import Hub, db, User, Spare
import requests
from time import sleep


@decorators.route('/api/test_init', methods=['GET'])
def test_init():
    db.drop_all()
    db.create_all()
    user = User(username='test')
    user.password = 'test'
    db.session.add(user)
    spare = Spare()
    spare.hub_id = 1
    spare.hours = "00:00 220 11|01:00 220 14|02:00 220 16"
    spare.days = "12.01 220 11|12.02 220 13|12.03 220 14"
    db.session.add(spare)
    hub = Hub(name='test', mac='AB:CD:EF:GH:IJ:KL')
    hub.user = user
    hub.spare = spare
    db.session.add(hub)
    db.session.commit()
    return 'well'


def hub_online(onenet_id='19959358'):
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
        return jsonify({'msg': 'no', 'error': 'need login'})
    hubs = []
    for hub in user.hubs:
        status = hub_online(hub.onenet_id)['online']
        hubs.append({'status': status}.update(hub.to_json()))
    return jsonify({'msg': 'ok', 'result': hubs})


@decorators.route('/api/hubs/spare/<id>', methods=['GET'])
def get_hub_spare(id):
    hub = Hub.query.filter_by(id=id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': 'hub not existed'})
    return jsonify({'msg': 'ok', 'result': hub.spare.to_json()})


@decorators.route('/api/hubs/timer/<id>', methods=['GET'])
def get_hub_timer(id):
    hub = Hub.query.filter_by(id=id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': 'hub not existed'})
    # !! not complete
    return jsonify({'msg': 'ok', 'result': hub.spare.to_json()})


@decorators.route('/api/hub/online', methods=['GET'])
def hub_online_():
    response = hub_online()
    return jsonify({'id': response['id'],
                    'protocol': response['protocol'],
                    'online': response['online']})


@decorators.route('/api/hub/<status>', methods=['GET'])
def hub_turn_on_or_off(status):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id=19959358&qos=1&timeout=100&type=0'
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
