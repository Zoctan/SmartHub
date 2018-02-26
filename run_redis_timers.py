#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import datetime

import schedule
import time
import threading
from app.tools.onenet import send_order
from app.tools.mysql import Mysql
from app.tools.redis import Redis


class Job:
    # 每5分钟检查一下数据库status=1的定时器是否在redis中，不存在的要添加，避免服务器宕机出现问题
    # 同时每次request保存到数据库的定时器也要加到redis中
    # 这里就不用crontab了，为了与服务器环境隔离
    def check_db_timers(self):
        # print("check_db_timers ", datetime.datetime.now())
        timer_list = Mysql().get_all_timer_by_status(1)
        for timer in timer_list:
            if not Redis().exists_with_prefix(timer.id + timer.hub_id, 'timers_'):
                # print('add to redis')
                timer.set()

    # 每2秒检查redis中的定时器
    def check_redis_timers(self):
        # print("check_redis_timers Begin: ", datetime.datetime.now())
        for key in Redis().get_all_key('timers_'):
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

    # 每天0点重置定时器的标志位isExecute
    def reset_redis_timers(self):
        print("reset redis timers:", datetime.datetime.now())
        for key in Redis().get_all_key('timers_'):
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
        schedule.every().day.at('00:00').do(self.run_thread, self.reset_redis_timers)

        while True:
            schedule.run_pending()


if __name__ == '__main__':
    Job().run()
    """
    for i in range(5):
        RedisTimer().set_repeat().set_power().set_time().set_status().set()
    for key in Redis().get_all_key():
        print(Redis().get(key).__dict__)
    print("Delete: ", Redis().delete_all_key())
    """
