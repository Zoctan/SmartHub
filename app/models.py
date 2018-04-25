#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import current_app
from itsdangerous import (
    TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from passlib.apps import custom_app_context as pwd_context

from app import db

# test_init
"""
db.session.remove()
db.drop_all()
db.create_all()

user = models.User()
user.avatar='http://smarthub.txdna.cn/test.png'
user.phone='1234567890'
user.email = '123456@qq.com'
user.username='test'
user.password = 'test'
db.session.add(user)
db.session.flush()

hub = models.Hub()
hub.name='可识别智能插座测试机'
hub.mac='2e:3a:e8:3e:7b:f8'
hub.room='room_bed'
hub.user_id=1
hub.onenet_id='19959358'
db.session.add(hub)
db.session.flush()

device = models.Device()
device.hub_id='19959358'
device.name='USB灯'
device.img='http://smarthub.txdna.cn/119959358.png'
device.eigenvalue=1
db.session.add(device)

device = models.Device()
device.hub_id='19959358'
device.name='USB风扇'
device.img='http://smarthub.txdna.cn/219959358.png'
device.eigenvalue=2
db.session.add(device)

device = models.Device()
device.hub_id='19959358'
device.name='充电宝'
device.img='http://smarthub.txdna.cn/319959358.png'
device.eigenvalue=3
db.session.add(device)

month = models.MonthSpare()
month.hub_id='19959358'
month.current_month=4
month.watt=24
db.session.add(month)

hour = models.HourSpare()
hour.hub_id='19959358'
hour.zero=7
hour.one=6
hour.two=4
hour.three=3
hour.four=2
hour.five=2
hour.six=3
hour.seven=5
hour.eight=6
hour.nine=8
hour.ten=12
hour.eleven=13
hour.twelve=15
hour.fourteen=14
hour.thirteen=12
hour.fourteen=11
hour.fifteen=16
hour.sixteen=14
hour.seventeen=18
hour.eighteen=21
hour.nineteen=22
hour.twenty=21
hour.twenty_one=24
hour.twenty_two=23
hour.twenty_three=14
db.session.add(hour)

timer = models.Timer(hub_id='19959358', name='下班', power=1, repeat='每天', time='16:55', status=0)
db.session.add(timer)
timer = models.Timer(hub_id='19959358', name='回家', power=0, repeat='一次性', time='16:54', status=0)
db.session.add(timer)


hub = models.Hub()
hub.name='临时插座1'
hub.mac='aa:aa:aa:aa:aa:aa'
hub.room='room_book'
hub.user_id=1
hub.onenet_id='7654321'
db.session.add(hub)
db.session.flush()

hub = models.Hub()
hub.name='临时插座2'
hub.mac='bb:bb:bb:bb:bb:bb'
hub.room='room_kitchen'
hub.user_id=1
hub.onenet_id='8765432'
db.session.add(hub)
db.session.flush()

month = models.MonthSpare()
month.hub_id='7654321'
month.current_month=4
month.watt=0
db.session.add(month)

month = models.MonthSpare()
month.hub_id='8765432'
month.current_month=4
month.watt=0
db.session.add(month)

hour = models.HourSpare()
hour.hub_id='7654321'
db.session.add(hour)

hour = models.HourSpare()
hour.hub_id='8765432'
db.session.add(hour)

db.session.commit()
"""


class User(db.Model):
    __tablename__ = 'smart_users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Unicode(32, collation='utf8_bin'),
            nullable=False, unique=True, index=True)
    password_hash = db.Column(db.Unicode(
            256, collation='utf8_bin'), nullable=False)
    avatar = db.Column(db.Unicode(256, collation='utf8_bin'),
            server_default="http://smarthub.txdna.cn/default.png",
            default="http://smarthub.txdna.cn/default.png")
    hubs = db.relationship('Hub', backref='User',
            lazy='dynamic', cascade='all, delete-orphan')
    phone = db.Column(db.Unicode(20, collation='utf8_bin'))
    email = db.Column(db.Unicode(30, collation='utf8_bin'))

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute')

    @password.setter
    def password(self, password):
        self.password_hash = pwd_context.hash(password)

    def verify_password(self, password):
        return pwd_context.verify(password, self.password_hash)

    def generate_auth_token(self, expiration=3600):
        s = Serializer(current_app.config['SECRET_KEY'], expires_in=expiration)
        return s.dumps({'id': self.id})

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except SignatureExpired:
            return None  # valid token, but expired
        except BadSignature:
            return None  # invalid token
        user = User.query.filter_by(id=data['id']).first()
        return user

    def to_json(self):
        json = {
            'id': self.id,
            'username': self.username,
            'avatar': self.avatar,
            'phone': self.phone,
            'email': self.email
        }
        return json

    def __repr__(self):
        return '<User(name={})>'.format(self.username)


class AnonymousUser(User):
    def __repr__(self):
        return '<AnonymousUser>'


class Hub(db.Model):
    __tablename__ = 'smart_hubs'
    onenet_id = db.Column(db.Unicode(
            64, collation='utf8_bin'), primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('smart_users.id'))
    name = db.Column(db.Unicode(64, collation='utf8_bin'),
            server_default='插座', default='插座')
    mac = db.Column(db.Unicode(64, collation='utf8_bin'), nullable=False)
    timers = db.relationship('Timer', backref='Hub',
            lazy='dynamic', cascade='all, delete-orphan')
    devices = db.relationship('Device', backref='Hub',
            lazy='dynamic', cascade='all, delete-orphan')
    room = db.Column(db.Unicode(64, collation='utf8_bin'),
            server_default='room_bed', default='room_bed')
    month_spare = db.relationship(
            'MonthSpare', backref='Hub', lazy='dynamic', cascade='all, delete-orphan')
    hour_spare = db.relationship(
            'HourSpare', backref='Hub', lazy='dynamic', cascade='all, delete-orphan')

    def to_json(self):
        json = {
            'onenet_id': self.onenet_id,
            'name': self.name,
            'mac': self.mac,
            'room': self.room
        }
        return json

    def __repr__(self):
        return '<Hub(name={})>'.format(self.name)


class Device(db.Model):
    __tablename__ = 'smart_devices'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'),
            db.ForeignKey('smart_hubs.onenet_id'))
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    img = db.Column(db.Unicode(256, collation='utf8_bin'))
    eigenvalue = db.Column(db.Integer, nullable=False)

    def to_json(self):
        json = {
            'id': self.id,
            'hub_id': self.hub_id,
            'name': self.name,
            'img': self.img,
            'eigenvalue': self.eigenvalue
        }
        return json

    def __repr__(self):
        return '<Device(name={}, eigenvalue={})>'.format(self.name, self.eigenvalue)


class Timer(db.Model):
    __tablename__ = 'smart_timers'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'),
            db.ForeignKey('smart_hubs.onenet_id'))
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    # 0: 关 1: 开
    power = db.Column(db.SmallInteger, server_default='0',
            default='0', nullable=False)
    # 每天|每周1-5|一次性
    repeat = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    # 15:26
    time = db.Column(db.Unicode(32, collation='utf8_bin'), nullable=False)
    # 0: 关 1: 开
    status = db.Column(db.SmallInteger, server_default='0',
            default='0', nullable=False)

    def to_json(self):
        json = {
            'id': self.id,
            'hub_id': self.hub_id,
            'name': self.name,
            'power': self.power,
            'repeat': self.repeat,
            'time': self.time,
            'status': self.status
        }
        return json

    def __repr__(self):
        return '<Timer(name={}, power={})>'.format(self.name, self.power)


# 添加插座时也要相应地建立该表和小时表
class MonthSpare(db.Model):
    __tablename__ = 'smart_month_spare'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'),
            db.ForeignKey('smart_hubs.onenet_id'))
    # 电价，暂时以广东为主
    price = db.Column(db.Float, server_default='0.6',
            default='0.6', nullable=False)
    watt = db.Column(db.Float, server_default='0.0', default='0.0')
    current_month = db.Column(db.SmallInteger, nullable=False)

    def to_json(self):
        hour = HourSpare.query.filter_by(hub_id=self.hub_id).first()
        json = {
            'id': self.id,
            'hub_id': self.hub_id,
            'price': self.price,
            'watt': self.watt,
            'current_month': self.current_month,
            'hour': hour.to_list()
        }
        return json

    def __repr__(self):
        return '<MonthSpare(price={}, watt={})>'.format(self.price, self.watt)


class HourSpare(db.Model):
    __tablename__ = 'smart_hour_spare'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'),
            db.ForeignKey('smart_hubs.onenet_id'))
    one = db.Column(db.Float, server_default='0.0', default='0.0')
    two = db.Column(db.Float, server_default='0.0', default='0.0')
    three = db.Column(db.Float, server_default='0.0', default='0.0')
    four = db.Column(db.Float, server_default='0.0', default='0.0')
    five = db.Column(db.Float, server_default='0.0', default='0.0')
    six = db.Column(db.Float, server_default='0.0', default='0.0')
    seven = db.Column(db.Float, server_default='0.0', default='0.0')
    eight = db.Column(db.Float, server_default='0.0', default='0.0')
    nine = db.Column(db.Float, server_default='0.0', default='0.0')
    ten = db.Column(db.Float, server_default='0.0', default='0.0')
    eleven = db.Column(db.Float, server_default='0.0', default='0.0')
    twelve = db.Column(db.Float, server_default='0.0', default='0.0')
    thirteen = db.Column(db.Float, server_default='0.0', default='0.0')
    fourteen = db.Column(db.Float, server_default='0.0', default='0.0')
    fifteen = db.Column(db.Float, server_default='0.0', default='0.0')
    sixteen = db.Column(db.Float, server_default='0.0', default='0.0')
    seventeen = db.Column(db.Float, server_default='0.0', default='0.0')
    eighteen = db.Column(db.Float, server_default='0.0', default='0.0')
    nineteen = db.Column(db.Float, server_default='0.0', default='0.0')
    twenty = db.Column(db.Float, server_default='0.0', default='0.0')
    twenty_one = db.Column(db.Float, server_default='0.0', default='0.0')
    twenty_two = db.Column(db.Float, server_default='0.0', default='0.0')
    twenty_three = db.Column(db.Float, server_default='0.0', default='0.0')
    zero = db.Column(db.Float, server_default='0.0', default='0.0')

    def to_list(self):
        return [
            self.zero, self.one, self.two, self.three, self.four, self.five, self.six, self.seven, self.eight,
            self.nine, self.ten, self.eleven, self.twelve, self.thirteen, self.fourteen, self.fifteen, self.sixteen,
            self.seventeen, self.eighteen, self.nineteen, self.twenty, self.twenty_one, self.twenty_two,
            self.twenty_three
        ]

    def __repr__(self):
        return '<HourSpare(month_id={})>'.format(self.month_id)
