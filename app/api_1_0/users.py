#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify, g

from . import decorators
from .authentication import verify_password, get_token
from ..models import User, db
from .qiniuyun import refresh_cdn


@decorators.composed(decorators.route('/api/tokens', methods=['POST']), decorators.json_required)
def login():
    username = request.json.get('username')
    password = request.json.get('password')
    if username and password and verify_password(username, password):
        return get_token()
    return jsonify({'msg': '用户名或密码错误', 'error': 1})


@decorators.route('/api/users', methods=['GET'])
def get_user_info():
    return jsonify({'msg': '用户信息获取成功', 'error': 0, 'result': [g.current_user.to_json()]})


@decorators.composed(decorators.route('/api/users', methods=['POST']), decorators.json_required)
def add_user():
    username = request.json.get('username')
    password = request.json.get('password')
    # require these value
    if not username or not password:
        return jsonify({'msg': '缺少参数: 用户名 密码', 'error': 1})
    if User.query.filter_by(username=username).first():
        return jsonify({'msg': '用户名已存在', 'error': 1})
    g.current_user = User(username=username)
    g.current_user.password = password
    db.session.add(g.current_user)
    db.session.flush()
    return get_token()


@decorators.composed(decorators.route('/api/users/avatar', methods=['PUT']), decorators.json_required)
def update_user_avatar():
    avatar = request.json.get('avatar')
    if not avatar or avatar == '':
        return jsonify({'msg': '图片链接不能为空', 'error': 1})
    g.current_user.avatar = avatar
    refresh_cdn([avatar])
    return jsonify({'msg': '头像修改成功', 'error': 0})


@decorators.composed(decorators.route('/api/users/password', methods=['PUT']), decorators.json_required)
def update_user_password():
    password = request.json.get('password')
    if not password:
        return jsonify({'msg': '密码不能为空', 'error': 1})
    g.current_user.password = password
    return jsonify({'msg': '密码修改成功', 'error': 0})


@decorators.composed(decorators.route('/api/users', methods=['PUT']), decorators.json_required)
def update_user():
    phone = request.json.get('phone')
    username = request.json.get('username')
    if not phone or not username:
        return jsonify({'msg': '用户名或手机号不能为空', 'error': 1})
    user = User.query.filter_by(username=username).first()
    if g.current_user.username != username and user:
        return jsonify({'msg': '用户名已存在', 'error': 1})
    g.current_user.phone = phone
    g.current_user.username = username
    return jsonify({'msg': '信息修改成功', 'error': 0})
