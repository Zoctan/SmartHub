#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import pickle
import redis


class Redis:
    default_prefix = 'smarthub_{}'

    def __init__(self, host='localhost', port=6379, password='root'):
        self.__redis = redis.Redis(host=host, port=port, password=password)

    def exists_with_prefix(self, key, key_prefix='{}'):
        key = key_prefix.format(key)
        key = self.default_prefix.format(key)
        return self.__redis.exists(key)

    def get(self, key):
        if self.__redis.exists(key):
            return pickle.loads(self.__redis.get(key))

    def get_with_prefix(self, key, key_prefix='{}'):
        key = key_prefix.format(key)
        key = self.default_prefix.format(key)
        return self.get(key)

    def set(self, key, value):
        # 序列化后再存进redis
        self.__redis.set(key, pickle.dumps(value))

    # 存的时候key加前缀
    def set_with_prefix(self, key, value, key_prefix='{}'):
        key = key_prefix.format(key)
        key = self.default_prefix.format(key)
        self.set(key, value)

    def delete(self, key):
        return self.__redis.delete(key)

    def delete_with_prefix(self, key, key_prefix='{}'):
        key = key_prefix.format(key)
        key = self.default_prefix.format(key)
        return self.delete(key)

    def get_all_key(self, key_prefix=''):
        key_prefix = self.default_prefix.format(key_prefix)
        return self.__redis.keys('{}*'.format(key_prefix))

    def delete_all_key(self, key_prefix=''):
        key_prefix = self.default_prefix.format(key_prefix)
        return self.__redis.delete(*self.get_all_key(key_prefix))
