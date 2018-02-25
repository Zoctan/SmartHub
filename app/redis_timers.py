#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import pickle
import redis
import schedule
import time
import pymysql
import threading
from app.api_1_0.hubs import send_order


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


class Job:
    # 每5分钟检查一下数据库status=1的定时器是否在redis中，不存在的要添加，避免服务器宕机出现问题
    # 同时每次request保存到数据库的定时器也要加到redis中
    # 这里就不用crontab了，为了与服务器环境隔离
    def check_db_timers(self):
        # print("check_db_timers ", datetime.datetime.now())
        timer_list = Mysql().get_all_timer_by_status(1)
        for timer in timer_list:
            if not Redis().exists_with_prefix(timer.id + timer.hub_id):
                # print('add to redis')
                timer.set()

    # 每2秒检查redis中的定时器
    def check_redis_timers(self):
        # print("check_redis_timers Begin: ", datetime.datetime.now())
        for key in Redis().get_all_key():
            # get_all_key拿出来的都是带前缀的key
            timer = Redis().get(key)
            if timer is None:
                continue
            # 先确认是否执行过
            if timer.isExecute:
                continue
            # 是否符合当前小时:分钟
            current_time = time.strftime('%H:%M', time.localtime(time.time()))
            if timer.time != current_time:
                continue
            # 是每周1-5重复，每天重复，还是一次性
            current_week_of_day = time.strftime("%A", time.localtime(time.time()))
            week_1_5_day = ['Monday', 'Tuesday', 'Wednesday', 'Thurday', 'Friday']
            if (timer.repeat == '每周1-5' and current_week_of_day in week_1_5_day) \
                    or timer.repeat == '每天' or timer.repeat == '一次性':
                msg = send_order(timer.hub_id, 'turn', timer.power)
                # print('执行Timer: ', msg)
            if timer.repeat == '一次性':
                # 一次性的执行完redis就删掉，并且数据库的status置为0
                Redis().delete(key)
                Mysql().update_timer_status(timer.id, timer.hub_id, 0)
            else:
                # 其他的则改变标志位
                timer.isExecute = True
                Redis().set(key, timer)
        # print("check_redis_timers End: ", datetime.datetime.now())

    # 每24小时重置定时器的标志位isExecute
    def reset_redis_timers(self):
        for key in Redis().get_all_key():
            timer = Redis().get(key)
            if timer is None:
                continue
            timer.isExecute = False
            Redis().set(key, timer)

    # 多线程定时任务
    def run_thread(self, func):
        job_thread = threading.Thread(target=func)
        job_thread.start()

    def run(self):
        schedule.every(5).minutes.do(self.run_thread, self.check_db_timers)
        schedule.every(2).seconds.do(self.run_thread, self.check_redis_timers)
        schedule.every(24).hours.do(self.run_thread, self.reset_redis_timers)

        while True:
            schedule.run_pending()


class Mysql:
    def __init__(self):
        # 打开数据库连接
        self.db = pymysql.connect("localhost", "root", "root", "smart", charset='utf8')
        # 使用 cursor() 方法创建一个游标对象 cursor
        self.cursor = self.db.cursor()

    def update_timer_status(self, id, hub_id, status):
        sql = "UPDATE smart_timers SET status = '{}' WHERE id = '{}' AND hub_id = '{}'".format(status, id, hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except:
            # 发生错误时回滚
            self.db.rollback()

    def get_all_timer_by_status(self, status):
        sql = "SELECT * FROM smart_timers WHERE status = '{}'".format(status)
        try:
            self.cursor.execute(sql)
            results = self.cursor.fetchall()
            timer_list = []
            for row in results:
                id = row[0]
                hub_id = row[1]
                name = row[2]
                power = row[3]
                repeat = row[4]
                time = row[5]
                status = row[6]
                timer = RedisTimer(id=id, hub_id=hub_id, repeat=repeat, time=time, power=power, status=status)
                timer_list.append(timer)
                # print("id=%s, hub_id=%s, name=%d, power=%s, repeat=%d, time=%d, status=%d" % (id, hub_id, name, power, repeat, time, status))
            return timer_list
        except:
            # print("Error: unable to fetch data")
            return

    def __del__(self):
        # 关闭数据库连接
        self.db.close()


if __name__ == '__main__':
    Job().run()
    """
    Mysql().update_timer_status(2, 19959358, 0)
    for i in range(5):
        RedisTimer().set_repeat().set_power().set_time().set_status().set()
    for key in Redis().get_all_key():
        print(Redis().get(key).__dict__)
    print("Delete: ", Redis().delete_all_key())
    """
