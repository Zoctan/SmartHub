#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import time
from flask import request
import base64
import hashlib
from ..tools.redis_watt import RedisWatt
from . import decorators


# 接收onenet发过来的数据
@decorators.route('/api/onenet', methods=['GET', 'POST'])
def onenet():
    # GET请求是一开始的第三方平台验证
    print(request.args)
    if request.method == 'GET':
        nonce = request.args.get('nonce')
        signature = request.args.get('signature')
        msg = request.args.get('msg')
        if nonce and signature and msg:
            tmp = hashlib.md5(('752481828' + nonce + msg).encode())
            if base64.b64encode(tmp.digest()).decode() == signature:
                return msg
    # POST请求是转发数据到第三方平台
    # 即转发到本服务器
    if request.method == 'POST':
        # 数据库只保存瓦数
        # 并且长驻后台脚本每5分钟才从redis更新到数据库
        # {"msg":{"at":1508948943731,"type":1,"ds_id":"W","value":0,"dev_id":19959358},"msg_signature":"i20/b74tIJvqMFek6q9Ffw==","nonce":"?@H&9p9E"}
        if request.json['msg']['ds_id'] == 'W':
            watt = request.json['msg']['value']
            dev_id = request.json['msg']['dev_id']
            current_hour = time.strftime('%H', time.localtime(time.time()))
            RedisWatt(dev_id=dev_id, watt=watt, current_hour=current_hour).update()
        return 'get'
    return 'error'
