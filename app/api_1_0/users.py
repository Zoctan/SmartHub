#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, g
from . import decorators
from .authentication import verify_password, get_token
from ..models import User, db
from .qiniuyun import refresh_cdn
from .result import Result


@decorators.route('/api/users', methods=['GET'])
def get_user_info():
    return get_token('用户信息获取成功')


@decorators.composed(decorators.route('/api/users', methods=['POST']), decorators.json_required)
def login_register():
    username = request.json.get('username')
    password = request.json.get('password')
    if not username or not password:
        return Result.error('缺少参数: 用户名 密码')
    # 可能是登录
    msg = '登录成功'
    if User.query.filter_by(username=username).first():
        if not verify_password(username, password):
            # 但登录的用户名或密码错误
            return Result.error('错误：用户名、密码错误或用户已注册，请重试')
    else:
        g.current_user = User(username=username)
        g.current_user.password = password
        db.session.add(g.current_user)
        db.session.flush()
        msg = '成功注册'
    return get_token(msg)


@decorators.composed(decorators.route('/api/users/avatar', methods=['PUT']), decorators.json_required)
def update_user_avatar():
    avatar = request.json.get('avatar')
    if not avatar or avatar == '':
        return Result.error('图片链接不能为空')
    g.current_user.avatar = 'http://smarthub.txdna.cn/' + avatar
    refresh_cdn([g.current_user.avatar])
    return Result.success('头像修改成功', g.current_user.avatar)


@decorators.composed(decorators.route('/api/users/password', methods=['PUT']), decorators.json_required)
def update_user_password():
    password = request.json.get('password')
    if not password:
        return Result.error('密码不能为空')
    g.current_user.password = password
    return Result.success('成功修改密码')


@decorators.composed(decorators.route('/api/users', methods=['PUT']), decorators.json_required)
def update_user():
    phone = request.json.get('phone')
    username = request.json.get('username')
    if not phone or not username:
        return Result.error('用户名或手机号不能为空')
    user = User.query.filter_by(username=username).first()
    if g.current_user.username != username and user:
        return Result.error('用户名已存在')
    g.current_user.phone = phone
    g.current_user.username = username
    return Result.success('成功修改信息')
