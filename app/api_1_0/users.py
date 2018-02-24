#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify, g

from . import decorators
from .authentication import verify_password, basic_auth_unauthorized, get_token
from ..models import User, db


@decorators.composed(decorators.route('/api/tokens', methods=['POST']), decorators.json_required)
def login():
    username = request.json.get('username')
    password = request.json.get('password')
    if username and password and verify_password(username, password):
        return get_token()
    return basic_auth_unauthorized('用户名或密码错误')


@decorators.route('/api/users', methods=['GET'])
def get_user_info():
    user = User.query.filter_by(id=g.current_user.id).first()
    return jsonify({'msg': 'ok', 'result': [user.to_json()]})


@decorators.composed(decorators.route('/api/users', methods=['POST']), decorators.json_required)
def add_user():
    username = request.json.get('username')
    password = request.json.get('password')
    # require these value
    if not username or not password:
        return jsonify({'msg': 'no', 'error': '缺少参数: 用户名 密码'})
    if User.query.filter_by(username=username).first():
        return jsonify({'msg': 'no', 'error': '用户名: {} 已存在'.format(username)})
    g.current_user = User(username=username)
    g.current_user.password = password
    db.session.add(g.current_user)
    return get_token()


@decorators.composed(decorators.route('/api/users/avatar', methods=['PUT']), decorators.json_required)
def update_user_avatar():
    avatar = request.json.get('avatar')
    if not avatar:
        return jsonify({'msg': 'no', 'error': '图片链接不能为空'})
    user = User.query.filter_by(id=g.current_user.id).first()
    user.avatar = avatar
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/users/password', methods=['PUT']), decorators.json_required)
def update_user_password():
    password = request.json.get('password')
    if not password:
        return jsonify({'msg': 'no', 'error': '密码不能为空'})
    user = User.query.filter_by(id=g.current_user.id).first()
    user.password = password
    return get_token()


@decorators.composed(decorators.route('/api/users', methods=['PUT']), decorators.json_required)
def update_user():
    phone = request.json.get('phone')
    username = request.json.get('username')
    if not phone or not username:
        return jsonify({'msg': 'no', 'error': '用户名或手机号不能为空'})
    user = User.query.filter_by(username=username).first()
    if user:
        return jsonify({'msg': 'no', 'error': '用户名已存在'})
    print(g.current_user)
    g.current_user.phone = phone
    g.current_user.username = username
    return jsonify({'msg': 'ok'})
