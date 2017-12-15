#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify, g
from . import decorators
from ..models import User, db
from .authentication import verify_password, basic_auth_unauthorized, get_token


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
    if not user:
        return jsonify({'msg': 'no', 'error': '用户不存在'})
    return jsonify({'msg': 'ok', 'result': [user.to_json()]})


@decorators.composed(decorators.route('/api/users', methods=['POST']), decorators.json_required)
def create_user():
    username = request.json.get('username')
    password = request.json.get('password')
    # require these value
    if not username or not password:
        return jsonify({'msg': 'no', 'error': '缺少参数: 需要用户名和密码'})
    if User.query.filter_by(username=username).first():
        return jsonify({'msg': 'no', 'error': '用户名: {} 已存在'.format(username)})
    g.current_user.password = password
    db.session.add(g.current_user)
    return get_token()


@decorators.composed(decorators.route('/api/users/avatar', methods=['PUT']), decorators.json_required)
def update_user_avatar():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not user:
        return jsonify({'msg': 'no', 'error': '用户不存在'})
    user.avatar = request.json.get('avatar')
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/users/password', methods=['PUT']), decorators.json_required)
def update_user_password():
    user = User.query.get(g.current_user.id)
    if not user:
        return jsonify({'msg': 'no', 'error': '用户不存在'})
    user.password = request.json.get('password')
    return jsonify({'msg': 'ok'})
