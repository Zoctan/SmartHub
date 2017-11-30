#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import g, jsonify
from flask_httpauth import HTTPBasicAuth
from ..models import User
from . import api

auth = HTTPBasicAuth()


@auth.error_handler
def unauthorized(error='unauthorized'):
    # request need login, return 403
    # 401: unauthorized, but it will alert a login window, so 403 instead of 401
    return jsonify({'msg': 'no', 'error': error}), 403


@auth.verify_password
def verify_password(token_or_username, password):
    # first try to authenticate by token
    user = User.verify_auth_token(token_or_username)
    if not user:
        # try to authenticate with username/password
        user = User.query.filter_by(username=token_or_username).first()
        if not user:
            return False
    g.current_user = user
    return user.verify_password(password)


@api.before_request
@auth.login_required
def before_request():
    pass


def get_token(duration=18000):
    token = g.current_user.generate_auth_token(duration)
    return jsonify({'msg': 'ok', 'result': [{'id': g.current_user.id,
                                             'token': token.decode('ascii'),
                                             'duration': duration}]})
