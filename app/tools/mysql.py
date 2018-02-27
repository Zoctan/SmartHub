#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import pymysql
import time

from .redis_timers import RedisTimer

dict_hour = {'00': 'zero', '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five', '06': 'six',
             '07': 'seven', '08': 'eight', '09': 'nine', '10': 'ten', '11': 'eleven', '12': 'twelve', '13': 'thirteen',
             '14': 'fourteen', '15': 'fifteen', '16': 'sixteen',
             '17': 'seventeen', '18': 'eighteen', '19': 'nineteen', '20': 'twenty', '21': 'twenty_one',
             '22': 'twenty_two', '23': 'twenty_three'}


class Mysql:
    def __init__(self):
        # 打开数据库连接
        self.db = pymysql.connect("localhost", "root", "root", "smart", charset='utf8')
        # 使用 cursor() 方法创建一个游标对象 cursor
        self.cursor = self.db.cursor()

    def update_timer_status(self, id, hub_id, status):
        sql = "UPDATE smart_timers SET status = '{}' WHERE `id` = '{}' AND `hub_id` = '{}'".format(status, id, hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except:
            # 发生错误时回滚
            self.db.rollback()

    def update_hour_spare(self, hub_id, column, value):
        sql = "UPDATE smart_hour_spare SET `{}` = '{}' WHERE `hub_id` = '{}'".format(column, value, hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except:
            self.db.rollback()

    def reset_hour_spare(self, hub_id):
        # 直接删掉旧的记录，再重建一条记录
        sql = "DELETE FROM smart_hour_spare WHERE `hub_id` = '{}'".format(hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except:
            self.db.rollback()

        sql = "INSERT INTO smart_hour_spare(hub_id) VALUES ('{}')".format(hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except:
            self.db.rollback()

    def update_month_spare(self, hub_id, current_hour):
        column = dict_hour[current_hour]
        select_hour_sql = "SELECT `{}` FROM smart_hour_spare WHERE `hub_id` = '{}'".format(column, hub_id)
        try:
            self.cursor.execute(select_hour_sql)
            watt = self.cursor.fetchone()[0]
        except Exception as e:
            print(e)
            return
        # 顺便更新当前月份
        current_month = int(time.strftime('%m', time.localtime(time.time())))
        sql = "UPDATE smart_month_spare SET `watt` = smart_month_spare.watt+'{}', `current_month` = '{}' WHERE `hub_id` = '{}'" \
            .format(watt, current_month, hub_id)
        try:
            self.cursor.execute(sql)
            self.db.commit()
        except Exception as e:
            print(e)
            self.db.rollback()

    def get_all_timer_by_status(self, status):
        sql = "SELECT * FROM smart_timers WHERE `status` = '{}'".format(status)
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
