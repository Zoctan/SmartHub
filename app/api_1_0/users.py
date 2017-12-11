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
    return basic_auth_unauthorized('login error')


@decorators.route('/api/users', methods=['GET'])
def get_user_info():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not user:
        return jsonify({'msg': 'no', 'error': 'user doesn\'t exist'})
    return jsonify({'msg': 'ok', 'result': [user.to_json()]})


@decorators.composed(decorators.route('/api/users', methods=['POST']), decorators.json_required)
def create_user():
    username = request.json.get('username')
    password = request.json.get('password')
    # require these value
    if not username or not password:
        return jsonify({'msg': 'no', 'error': 'missing arguments, require: username, password'})
    if User.query.filter_by(username=username).first():
        return jsonify({'msg': 'no', 'error': 'username: {} is already existed'.format(username)})
    g.current_user.password = password
    db.session.add(g.current_user)
    return get_token()


@decorators.composed(decorators.route('/api/users/avatar', methods=['POST']), decorators.json_required)
def update_user_avatar():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not user:
        return jsonify({'msg': 'no', 'error': 'user doesn\'t exist'})
    user.avatar = request.json.get('avatar')
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/users/password', methods=['POST']), decorators.json_required)
def update_user_password():
    user = User.query.get(g.current_user.id)
    if not user:
        return jsonify({'msg': 'no', 'error': 'user doesn\'t exist'})
    user.password = request.json.get('password')
    return jsonify({'msg': 'ok'})
