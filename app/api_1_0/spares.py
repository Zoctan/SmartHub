#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from . import decorators
from ..models import MonthSpare
from .result import Result


@decorators.route('/api/hubs/spares/<onenet_id>', methods=['GET'])
def get_hub_spare(onenet_id):
    month = MonthSpare.query.filter_by(hub_id=onenet_id).first()
    if not month:
        return Result.error('插座不存在')
    return Result.success('成功获取插座能耗', month.to_json())
