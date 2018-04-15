#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from functools import wraps
from flask import request
from . import api
from ..models import db
from .result import Result


def route(rule, **options):
    def decorator(func):
        @wraps(func)
        def decorated_function(*args, **kwargs):
            rest = func(*args, **kwargs)
            try:
                db.session.commit()
                return rest
            except:
                db.session.rollback()
                return Result.error('数据库出错')
            finally:
                db.session.remove()

        endpoint = options.pop('endpoint', None)
        api.add_url_rule(rule, endpoint, decorated_function, **options)
        return decorated_function

    return decorator


def composed(*decorators):
    # compose 2 or more decorator
    def decorator(func):
        for dec in reversed(decorators):
            func = dec(func)
        return func

    return decorator


def json_required(func):
    @wraps(func)
    def decorated_function(*args, **kwargs):
        if not request.json:
            return Result.error('请使用Json格式传输数据')
        return func(*args, **kwargs)

    return decorated_function
