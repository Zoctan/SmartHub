#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import jsonify

from . import api


@api.app_errorhandler(400)
def error_request(error):
    return jsonify({'msg': '请求错误', 'error': 1}), 400


@api.app_errorhandler(403)
def forbidden(error):
    return jsonify({'msg': '禁止访问', 'error': 1}), 403


@api.app_errorhandler(404)
def not_found(error):
    return jsonify({'msg': '无法访问', 'error': 1}), 404


@api.app_errorhandler(405)
def error_method(error):
    return jsonify({'msg': '请求方法错误', 'error': 1}), 405


@api.app_errorhandler(408)
def time_out(error):
    return jsonify({'msg': '超时', 'error': 1}), 408


@api.app_errorhandler(500)
def internal_error(error):
    return jsonify({'msg': '内部错误', 'error': 1}), 500


@api.app_errorhandler(503)
def unavailable(error):
    return jsonify({'msg': '服务不可达', 'error': 1}), 503
