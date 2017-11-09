#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template, request, jsonify
from . import smart
from time import sleep
from subprocess import run, PIPE, STDOUT

crontab = '/etc/crontab'
# crontab = '/tmp/crontab'


def get_once():
    p = run("""atq | awk '{print $1" "$5}' | awk -F: '{print $1" "$2}'""", shell=True, stdout=PIPE, stderr=STDOUT)
    id_hour_minute = p.stdout.decode().split('\n')
    crontabs = []
    for i in id_hour_minute[0:-1]:
        _id, hour, minute = i.split(' ')[0], i.split(' ')[1], i.split(' ')[2]
        p = run(['at', '-c', _id], stdout=PIPE, stderr=STDOUT)
        am_pm = 'AM' if int(hour) < 12 else 'PM'
        _time = '{}:{} {}'.format(hour, minute, am_pm)
        if '/hub/on' in p.stdout.decode():
            crontabs.append({'id': 'power_on', 'name': '定时开机',
                             'status': 'checked', 'time': _time, 'repeat': '一次性'})
        elif '/hub/off' in p.stdout.decode():
            crontabs.append({'id': 'power_off', 'name': '定时关机',
                             'status': 'checked', 'time': _time, 'repeat': '一次性'})
    return crontabs


def set_once(hour, minute, command):
    run(['at', 'now', '+{}minutes'.format(hour * 3600 + minute)], input=command.encode(), stdout=PIPE, stderr=STDOUT)


def rm_once(key_word):
    p = run("""atq | awk '{print $1}'""", shell=True, stdout=PIPE, stderr=STDOUT)
    _id = p.stdout.decode().split('\n')
    for i in _id[0:-1]:
        p = run(['at', '-c', i], stdout=PIPE, stderr=STDOUT)
        if key_word in p.stdout.decode():
            run(['atrm', i])


@smart.route('/crontabs', methods=['GET'])
def get_all_crontabs():
    with open(crontab, 'r') as f:
        task = [line for line in f.readlines() if '/hub/on' in line or '/hub/off' in line]
    crontabs = []
    _once = get_once()
    if _once:
        crontabs += _once
    if not task and not crontabs:  # empty
        for i in zip(['power_on', 'power_off'], ['定时开机', '定时关机']):
            crontabs.append({'id': i[0], 'name': i[1], 'status': '', 'time': ''})
    else:
        for i in task:
            if '/hub/on' in i:
                _id = 'power_on'
                name = '定时开机'
            else:
                _id = 'power_off'
                name = '定时关机'
            i = i.split(' ')
            hour, minute = i[1], i[0]
            am_pm = 'AM' if int(hour) < 12 else 'PM'
            _time = '{}:{} {}'.format(hour, minute, am_pm)
            if i[2] == i[3] == i[4] == '*':
                repeat = '每天'
            else:
                repeat = '每周' + i[4]
            crontabs.append({'id': _id, 'name': name,
                            'status': 'checked', 'time': _time, 'repeat': repeat})
    if len(crontabs) == 1:  # not enough
        for i in zip(['power_on', 'power_off'], ['定时开机', '定时关机']):
            if crontabs[0]['id'] != i[0]:
                crontabs.append({'id': i[0], 'name': i[1], 'status': '', 'time': ''})
    return render_template('crontabs.html', id='crontabs', crontabs=crontabs)


def operation_crontab(op, key_word, task):
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


@smart.route('/crontabs/<operation>', methods=['POST'])
def set_crontabs(operation):
    task = request.json
    print(task)
    minute, hour = task['minute'], task['hour']
    if task['repeat'] == '每天':
        day, month, week = '*', '*', '*'
    elif task['repeat'] == '每周1-5':
        day, month, week = '*', '*', '1-5'
    else:
        day, month, week = '*', '*', ','.join(task['repeat'])
    if task['which'] == 'power_on':
        key_word = '/hub/on'
    else:
        key_word = '/hub/off'
    command = 'curl smart.txdna.cn' + key_word + ' > /tmp/curl_hub'
    user = 'root'
    if task['repeat'] != '一次性':
        task = '{} {} {} {} {} {} {}'.format(
                minute, hour, day, month, week, user, command)
        operation_crontab(operation, key_word, task)
    else:
        hour, minute = int(hour), int(minute)
        if operation == 'add':
            set_once(hour, minute, command)
        if operation == 'delete':
            rm_once(key_word)
        else:
            rm_once(key_word)
            set_once(hour, minute, command)
    print(task)
    sleep(1)
    return jsonify({'msg': 'ok'})
