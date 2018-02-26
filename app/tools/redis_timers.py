#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from .redis import Redis


class RedisTimer:
    def __init__(self, id=-1, hub_id=-1, repeat='每天', time='00:00', power=0, status=0):
        self.id = str(id)
        self.hub_id = str(hub_id)
        self.repeat = repeat
        self.time = time
        self.power = power
        # 定时器只有开启才会进入redis执行
        self.status = status
        # 是否执行过
        self.isExecute = False

    def set(self):
        # 存RedisTimer这个对象
        # 定时器id和对应的插座id拼接，确保key无重复，以及便于删除
        Redis().set_with_prefix(self.id + self.hub_id, self, 'timers_')

    def delete(self):
        Redis().delete_with_prefix(self.id + self.hub_id, 'timers_')

    def update(self):
        self.delete()
        self.set()
