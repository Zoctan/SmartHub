#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import request, jsonify, g
from . import decorators
from ..models import User, db
from .authentication import verify_password, unauthorized, get_token


@decorators.composed(decorators.route('/api/tokens', methods=['POST']), decorators.json_required)
def login():
    username = request.json.get('username')
    password = request.json.get('password')
    print("username", username)
    print("password", password)
    if username and password and verify_password(username, password):
        return get_token()
    return unauthorized('login error')


@decorators.route('/api/tokens', methods=['DELETE'])
def logout():
    return jsonify({'msg': 'ok'})


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


@decorators.composed(decorators.route('/api/users/avatar', methods=['PUT']), decorators.json_required)
def update_user_avatar():
    user = User.query.filter_by(id=g.current_user.id).first()
    if not request.json.get('avatar'):
        return jsonify({'msg': 'no', 'error': 'null'})
    user.avatar = request.json.get('avatar')
    return jsonify({'msg': 'ok'})


@decorators.composed(decorators.route('/api/users/password', methods=['PUT']), decorators.json_required)
def update_user_password():
    old = User.query.get(g.current_user.id)
    if not set(request.json.keys()) == {'oldpassword', 'newpassword'}:
        return jsonify({'msg': 'no', 'error': 'can not modify'})
    # check old password that is correct or not
    # after modify, must logout!
    if not verify_password(old.username, request.json.get('oldpassword')):
        return jsonify({'msg': 'no', 'error': 'old password not correct'})
    old.password = request.json.get('newpassword')
    return jsonify({'msg': 'ok'})
