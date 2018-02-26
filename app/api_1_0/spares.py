#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import jsonify, g
from . import decorators
from ..models import MonthSpare


@decorators.route('/api/hubs/spares/<device_id>', methods=['GET'])
def get_hub_spare(device_id):
    if g.current_user is None:
        return jsonify({'msg': 'no', 'result': '请带上token查询'})
    month = MonthSpare.query.filter_by(hub_id=device_id).first()
    if not month:
        return jsonify({'msg': 'no', 'error': '插座不存在'})
    return jsonify({'msg': 'ok', 'result': month.to_json()})
