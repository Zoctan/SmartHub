#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import jsonify
from . import decorators
from ..models import MonthSpare, Hub


@decorators.route('/api/hubs/spares/<onenet_id>', methods=['GET'])
def get_hub_spare(onenet_id):
    hub = Hub.query.filter_by(onenet_id=onenet_id).first()
    if not hub:
        return jsonify({'msg': '插座不存在', 'error': 1})
    month = MonthSpare.query.filter_by(hub_id=onenet_id).first()
    if not month:
        return jsonify({'msg': '插座不存在', 'error': 1})
    return jsonify({'msg': '成功获取插座能耗', 'error': 0, 'result': month.to_json()})
