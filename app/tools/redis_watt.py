#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from .redis import Redis


class RedisWatt:
    default_prefix = 'watt_'

    def __init__(self, dev_id=-1, watt=0, current_hour='00'):
        self.dev_id = str(dev_id)
        self.watt = watt
        self.current_hour = current_hour

    def update(self):
        # redis只保存当前小时平均值
        # 先从redis中取出上一次的值，然后和当前值加起来取平均
        # 为了和定时器区分，前缀要改
        last = Redis().get_with_prefix(key=self.dev_id, key_prefix=self.default_prefix)
        if last is not None:
            self.watt = (last.watt + self.watt) / 2.0
        Redis().set_with_prefix(key=self.dev_id, value=self, key_prefix=self.default_prefix)

    def delete(self):
        Redis().delete_with_prefix(key=self.dev_id, key_prefix=self.default_prefix)
