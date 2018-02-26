#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from .redis import Redis


class RedisWatt:
    def __init__(self, dev_id=-1, watt=0, current_hour='00'):
        self.dev_id = str(dev_id)
        self.watt = watt
        self.current_hour = current_hour

    def update(self):
        # redis只保存当前小时平均值
        # 先从redis中取出上一次的值，然后和当前值加起来取平均
        # 为了和定时器区分，前缀要改
        last = Redis().get_with_prefix(self.dev_id, 'watt_')
        if last is not None:
            self.watt = (last.watt + self.watt) / 2.0
        Redis().set_with_prefix(self.dev_id, self, 'watt_')

    def delete(self):
        Redis().delete_with_prefix(self.dev_id, 'watt_')
