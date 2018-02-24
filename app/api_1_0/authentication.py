#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import g, jsonify
from flask_httpauth import HTTPBasicAuth, HTTPTokenAuth, MultiAuth

from . import api
from ..models import User, AnonymousUser

basic_auth = HTTPBasicAuth()
token_auth = HTTPTokenAuth(scheme='Smart')
multi_auth = MultiAuth(basic_auth, token_auth)


@basic_auth.error_handler
def basic_auth_unauthorized(error):
    # default 401: unauthorized, but it will alert a login window
    return jsonify({'msg': 'no', 'error': error})


@token_auth.error_handler
def token_auth_unauthorized(error='请带上token访问api'):
    return jsonify({'msg': 'no', 'error': error})


@basic_auth.verify_password
def verify_password(username, password):
    if not username:
        g.current_user = AnonymousUser()
        return True
    # try to authenticate with username/password
    user = User.query.filter_by(username=username).first()
    g.current_user = user
    return False if not user else user.verify_password(password)


@token_auth.verify_token
def verify_token(token):
    user = User.verify_auth_token(token)
    g.current_user = user
    return False if not user else True


@api.before_request
@multi_auth.login_required
def before_request():
    pass


def get_token():
    token = g.current_user.generate_auth_token(31536000)
    result = g.current_user.to_json()
    result.update({'token': token.decode('ascii')})
    return jsonify({'msg': 'ok', 'result': [result]})
