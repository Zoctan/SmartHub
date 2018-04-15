#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from . import api
from .result import Result


@api.app_errorhandler(400)
def error_request(error):
    return Result.error('请求错误')


@api.app_errorhandler(403)
def forbidden(error):
    return Result.error('禁止访问')


@api.app_errorhandler(404)
def not_found(error):
    return Result.error('无法访问')


@api.app_errorhandler(405)
def error_method(error):
    return Result.error('请求方法错误')


@api.app_errorhandler(408)
def time_out(error):
    return Result.error('超时')


@api.app_errorhandler(500)
def internal_error(error):
    return Result.error('内部错误')


@api.app_errorhandler(503)
def unavailable(error):
    return Result.error('服务不可达')
