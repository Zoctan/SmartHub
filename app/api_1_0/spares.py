#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import jsonify, g
from . import decorators
from ..models import MonthSpare


@decorators.route('/api/hubs/spares/<device_id>', methods=['GET'])
def get_hub_spare(device_id):
    if g.current_user is None:
        return jsonify({'msg': '请带上token查询', 'error': 1})
    month = MonthSpare.query.filter_by(hub_id=device_id).first()
    if not month:
        return jsonify({'msg': '插座不存在', 'error': 1})
    return jsonify({'msg': '成功获取插座能耗', 'error': 0, 'result': month.to_json()})
