#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import g, jsonify
from flask_httpauth import HTTPBasicAuth, HTTPTokenAuth, MultiAuth

from . import api
from ..models import User, AnonymousUser

scheme = 'Smart'

basic_auth = HTTPBasicAuth()
token_auth = HTTPTokenAuth(scheme=scheme)
multi_auth = MultiAuth(basic_auth, token_auth)


@basic_auth.error_handler
def basic_auth_unauthorized():
    # default 401: unauthorized, but it will alert a login window
    return jsonify({'msg': '请带上token访问api', 'error': 1})


@token_auth.error_handler
def token_auth_unauthorized():
    return jsonify({'msg': '请带上token访问api', 'error': 1})


@basic_auth.verify_password
def verify_password(username, password):
    if not username:
        g.current_user = AnonymousUser()
        return True
    # try to authenticate with username/password
    user = User.query.filter_by(username=username).first()
    g.current_user = user
    return False if user is None else user.verify_password(password)


@token_auth.verify_token
def verify_token(token):
    user = User.verify_auth_token(token)
    g.current_user = user
    return False if user is None else True


@api.before_request
@multi_auth.login_required
def before_request():
    pass


def get_token(msg='token获取成功'):
    token = g.current_user.generate_auth_token(31536000)
    result = g.current_user.to_json()
    result.update({'token': '{} {}'.format(scheme, token.decode('ascii'))})
    return jsonify({'msg': msg, 'error': 0, 'result': result})
