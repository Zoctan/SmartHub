#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import request, jsonify
from app.models import Hub
from . import api
from time import sleep
from subprocess import run, PIPE, STDOUT

crontab = '/etc/crontab'

# crontab = '/tmp/crontab'


@api.route('/api/hubs/timers/<device_id>', methods=['GET'])
def get_hub_timers(device_id):
    hub = Hub.query.filter_by(onenet_id=device_id).first()
    if not hub:
        return jsonify({'msg': 'no', 'error': 'hub not existed'})
    with open(crontab, 'r') as f:
        hub_on = '/hubs/on/{}'.format(device_id)
        hub_off = '/hubs/off/{}'.format(device_id)
        task = [line for line in f.readlines() if hub_on in line or hub_off in line]
    crontabs = []
    _once = get_once(device_id)
    if _once:
        crontabs += _once
    if not task and not crontabs:  # empty
        for i in ['定时开机', '定时关机']:
            crontabs.append({'name': i, 'time': '', 'repeat': ''})
    else:
        for i in task:
            if '/hubs/on' in i:
                name = '定时开机'
            else:
                name = '定时关机'
            i = i.split(' ')
            hour, minute = i[1], i[0]
            am_pm = 'AM' if int(hour) < 12 else 'PM'
            _time = '{}:{} {}'.format(hour, minute, am_pm)
            if i[2] == i[3] == i[4] == '*':
                repeat = '每天'
            else:
                repeat = '每周' + i[4]
            crontabs.append({'name': name, 'time': _time, 'repeat': repeat})
    if len(crontabs) == 1:  # not enough
        for i in ['定时开机', '定时关机']:
            if crontabs[0]['name'] != i:
                crontabs.append({'name': i, 'time': '', 'repeat': ''})
    print(crontabs)
    return jsonify({'msg': 'ok', 'result': crontabs})


@api.route('/api/hubs/timers/<operation>', methods=['POST'])
def set_timer(operation):
    task = request.json
    # print(task)
    minute, hour = task['minute'], task['hour']
    if task['which'] == 'power_on':
        key_word = '/hubs/on/{}'.format(task['device_id'])
    else:
        key_word = '/hubs/off/{}'.format(task['device_id'])
    command = 'curl https://smart.txdna.cn/api' + key_word + ' > /tmp/curl_hub'
    if task['repeat'] == '一次性':
        if operation == 'add':
            set_once(minute, command)
        if operation == 'delete':
            rm_once(command)
        else:
            rm_once(command)
            set_once(minute, command)
    else:
        if task['repeat'] == '每天':
            day, month, week = '*', '*', '*'
        elif task['repeat'] == '每周1-5':
            day, month, week = '*', '*', '1-5'
        else:  # custom
            day, month, week = '*', '*', ','.join(task['repeat'])
        user = 'root'
        task = '{} {} {} {} {} {} {}'.format(
                minute, hour, day, month, week, user, command)
        write_crontab(operation, key_word, task)
    # print(task)
    sleep(1)
    return jsonify({'msg': 'ok'})


def write_crontab(op, key_word, task):
    # 将文件读取到内存中
    with open(crontab, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    if op == 'add':
        with open(crontab, 'a', encoding='utf-8') as f_w:
            f_w.write(task + '\n')
            return
    # 写的方式打开文件
    with open(crontab, 'w', encoding='utf-8') as f_w:
        for i in range(len(lines)):
            if key_word in lines[i]:
                if op == 'replace':
                    lines[i] = task + '\n'
                elif op == 'delete':
                    lines[i] = ''
        f_w.writelines(lines)


def get_once(device_id):
    p = run("""atq | awk '{print $1" "$5}' | awk -F: '{print $1" "$2}'""", shell=True, stdout=PIPE, stderr=STDOUT)
    id_hour_minute = p.stdout.decode().split('\n')
    crontabs = []
    hub_on = '/hubs/on/{}'.format(device_id)
    hub_off = '/hubs/off/{}'.format(device_id)
    for i in id_hour_minute[0:-1]:
        _id, hour, minute = i.split(' ')[0], i.split(' ')[1], i.split(' ')[2]
        p = run(['at', '-c', _id], stdout=PIPE, stderr=STDOUT)
        am_pm = 'AM' if int(hour) < 12 else 'PM'
        _time = '{}:{} {}'.format(hour, minute, am_pm)
        if hub_on in p.stdout.decode():
            crontabs.append({'name': '定时开机', 'time': _time, 'repeat': '一次性'})
        elif hub_off in p.stdout.decode():
            crontabs.append({'name': '定时关机', 'time': _time, 'repeat': '一次性'})
    return crontabs


def set_once(minute, command):
    run(['at', 'now', '+{}minutes'.format(minute)], input=command.encode(), stdout=PIPE, stderr=STDOUT)


def rm_once(command):
    p = run("""atq | awk '{print $1}'""", shell=True, stdout=PIPE, stderr=STDOUT)
    _id = p.stdout.decode().split('\n')
    for i in _id[0:-1]:
        p = run(['at', '-c', i], stdout=PIPE, stderr=STDOUT)
        if command in p.stdout.decode():
            run(['atrm', i])
