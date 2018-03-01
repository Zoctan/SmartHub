#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from time import sleep
import requests

headers = {'api-key': 'nJVyiaj5Y297Fc6Q=bUYVWnz2=0='}


# https://open.iot.10086.cn/doc/art257.html#68
# 发送命令
def send_order(device_id, order, status, sleep_time=4):
    # https://open.iot.10086.cn/doc/art257.html#68
    url = 'http://api.heclouds.com/cmds'
    cmd_url = url + '?device_id={}&qos=1&timeout=100&type=0'.format(device_id)
    data = None
    status = str(status)
    if order == 'turn':
        # 继电器开关
        data = '{Relay}' + status
    elif order == 'reset':
        # 清除存储的所有用电器特征值数据
        data = '{reset}' + status
    elif order == 'store':
        # 存储当前的用电器的特征
        data = '{store}' + status
    elif order == 'match':
        # 识别当前的用电器，更新list的值
        data = '{match}' + status
    if data is None:
        return '命令错误'
    else:
        response = requests.post(cmd_url, data=data, headers=headers)
        # 4秒收一次数据，只能延迟高点查询插座状态
        sleep(sleep_time)
        if order == 'store' or order == 'match':
            if order == 'store':
                url = 'http://api.heclouds.com/devices/{}/datastreams/STORE'.format(device_id)
            if order == 'match':
                url = 'http://api.heclouds.com/devices/{}/datastreams/list'.format(device_id)
            sleep(2)
            response = requests.get(url, headers=headers)
            return response.json()['data']['current_value'], None
        query_url = url + '/' + response.json()['data']['cmd_uuid']
        query_response = requests.get(query_url, headers=headers)
        status = query_response.json()['data']['status']
        if status == 0:
            msg = '设备不在线'
        elif status == 1:
            msg = '命令已创建'
        elif status == 2:
            msg = '命令已发往设备'
        elif status == 3:
            msg = '命令发往设备失败'
        elif status == 4:
            msg = '设备正常响应'
        elif status == 5:
            msg = '命令执行超时'
        else:
            msg = '设备响应消息过长'
        return msg, query_url
