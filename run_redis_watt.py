#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import datetime

import schedule
import threading
from app.tools.mysql import Mysql, dict_hour
from app.tools.redis import Redis


class Job:
    # 每5分钟从redis更新到数据库对应的小时表
    def update_redis_watt_to_db(self):
        # print("check_redis_timers Begin: ", datetime.datetime.now())
        for key in Redis().get_all_key('watt_'):
            redis_watt = Redis().get(key)
            if redis_watt is None:
                continue
            column = dict_hour[redis_watt.current_hour]
            value = redis_watt.watt
            hub_id = redis_watt.dev_id
            Mysql().update_hour_spare(hub_id=hub_id, column=column, value=value)

    # 每30分钟从redis更新到数据库对应的月表
    def update_watt_to_db_month(self):
        # print("check_redis_timers Begin: ", datetime.datetime.now())
        for key in Redis().get_all_key('watt_'):
            redis_watt = Redis().get(key)
            if redis_watt is None:
                continue
            Mysql().update_month_spare(hub_id=redis_watt.dev_id, current_hour=redis_watt.current_hour)

    # 每天0点重置数据库的小时表
    def reset_db_hour_spare(self):
        print("reset db hour spare:", datetime.datetime.now())
        for key in Redis().get_all_key('watt_'):
            redis_watt = Redis().get(key)
            if redis_watt is None:
                continue
            Mysql().reset_hour_spare(hub_id=redis_watt.dev_id)

    # 多线程定时任务
    def run_thread(self, func):
        job_thread = threading.Thread(target=func)
        job_thread.start()

    def run(self):
        schedule.every(5).minutes.do(self.run_thread, self.update_redis_watt_to_db)
        schedule.every(30).minutes.do(self.run_thread, self.update_watt_to_db_month)
        schedule.every().day.at('00:00').do(self.run_thread, self.reset_db_hour_spare)

        while True:
            schedule.run_pending()


if __name__ == '__main__':
    Job().run()
    """
    from app.tools.redis_watt import RedisWatt
    from app.tools.redis_timers import RedisTimer

    for i in range(1, 5):
        RedisWatt(dev_id=i).update()
        RedisTimer(id=-1, hub_id=i).set()
    for w in Redis().get_all_key('watt_'):
        print(w)
    print(Redis().delete_all_key('watt_'))
    for t in Redis().get_all_key('timers_'):
        print(t)
    """
