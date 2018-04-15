#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import jsonify


class Result:
    @staticmethod
    def success(msg, result=None):
        return jsonify({'msg': msg, 'error': 0, 'result': result}), {'Content-Type': 'application/json;charset=utf-8'}

    @staticmethod
    def error(msg, result=None):
        return jsonify({'msg': msg, 'error': 1, 'result': result}), {'Content-Type': 'application/json;charset=utf-8'}
