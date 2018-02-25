#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import pickle
import redis


class RedisTimer:
    def __init__(self, id=-1, hub_id=-1, repeat='每天', time='00:00', power=0, status=0):
        self.id = str(id)
        self.hub_id = str(hub_id)
        self.repeat = repeat
        self.time = time
        self.power = power
        self.status = status
        self.isExecute = False

    def set(self):
        # 存RedisTimer这个对象
        # 定时器id和对应的插座id拼接，确保key无重复，以及便于删除
        Redis().set_with_prefix(self.id + self.hub_id, self)

    def delete(self):
        Redis().delete_with_prefix(self.id + self.hub_id)

    def update(self):
        Redis().delete_with_prefix(self.id + self.hub_id)
        Redis().set_with_prefix(self.id + self.hub_id, self)


class Redis:
    def __init__(self, host='localhost', port=6379, password='root'):
        self.__redis = redis.Redis(host=host, port=port, password=password)

    def exists_with_prefix(self, key, key_prefix='smarthub_{}'):
        key = key_prefix.format(key)
        return self.__redis.exists(key)

    def get(self, key):
        if self.__redis.exists(key):
            return pickle.loads(self.__redis.get(key))

    def get_with_prefix(self, key, key_prefix='smarthub_{}'):
        key = key_prefix.format(key)
        return self.get(key)

    def set(self, key, value):
        # 序列化后再存进redis
        self.__redis.set(key, pickle.dumps(value))

    # 存的时候key加前缀
    def set_with_prefix(self, key, value, key_prefix='smarthub_{}'):
        key = key_prefix.format(key)
        self.set(key, value)

    def delete(self, key):
        return self.__redis.delete(key)

    def delete_with_prefix(self, key, key_prefix='smarthub_{}'):
        key = key_prefix.format(key)
        return self.delete(key)

    def get_all_key(self, key_prefix='smarthub_'):
        return self.__redis.keys('{}*'.format(key_prefix))

    def delete_all_key(self, key_prefix='smarthub_'):
        return self.__redis.delete(*self.get_all_key(key_prefix))
