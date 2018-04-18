#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request
from . import decorators
from app import mail
from flask_mail import Message
from .result import Result
from threading import Thread
from manage import app


# 异步发送邮件
def send_async_email(app, msg):
    with app.app_context():
        mail.send(msg)


@decorators.composed(decorators.route('/api/mail', methods=['POST']), decorators.json_required)
def index():
    email = request.json.get('email')
    phone = request.json.get('phone')
    if not email or not phone:
        return Result.error('为了确保收到回复，手机和邮箱至少有一个不为空')
    msg = Message(subject='SmartHub-反馈信息', recipients=['752481828@qq.com'])
    # 邮件内容会以文本和html两种格式呈现，而你能看到哪种格式取决于你的邮件客户端
    msg.body = '{} \r\n反馈者邮箱：{} 手机：{}'.format(request.json.get('msg'), email, phone)
    msg.html = '{} <br />反馈者邮箱：{} 手机：{}'.format(request.json.get('msg'), email, phone)
    thread = Thread(target=send_async_email, args=[app, msg])
    thread.start()
    return Result.success('邮件发送成功')
