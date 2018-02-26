#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import current_app
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from passlib.apps import custom_app_context as pwd_context

from app import db

# test_init
"""
db.session.remove()
db.drop_all()
db.create_all()
user = models.User()
user.phone='13192605482'
user.username='test'
user.password = 'test'
db.session.add(user)
db.session.flush()
hub = models.Hub()
hub.name='可识别智能插座测试机'
hub.mac='AB:CD:EF:GH:IJ:KL'
hub.user_id=1
hub.onenet_id='19959358'
db.session.add(hub)
db.session.flush()
month = models.MonthSpare()
month.hub_id='19959358'
month.current_month=1
month.watt=68
db.session.add(month)
hour = models.HourSpare()
hour.hub_id='19959358'
hour.zero=12
hour.thirteen=6
hour.fourteen=3
hour.twenty_three=23
db.session.add(hour)
timer = models.Timer(hub_id='19959358', name='test', power=1, repeat='每天', time='16:55', status=1)
db.session.add(timer)
timer = models.Timer(hub_id='19959358', name='test2', power=0, repeat='一次性', time='16:54', status=1)
db.session.add(timer)
db.session.commit()
"""

class User(db.Model):
    __tablename__ = 'smart_users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Unicode(32, collation='utf8_bin'), nullable=False, unique=True, index=True)
    password_hash = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    avatar = db.Column(db.Unicode(256, collation='utf8_bin'),
            server_default="http://p0qgwnuel.bkt.clouddn.com/server_default.png")
    hubs = db.relationship('Hub', backref='User', lazy='dynamic', cascade='all, delete-orphan')
    phone = db.Column(db.Unicode(20, collation='utf8_bin'))

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
            'phone': self.phone
        }
        return json

    def __repr__(self):
        return '<User(name={})>'.format(self.username)


class AnonymousUser(User):
    def __repr__(self):
        return '<AnonymousUser>'


class Hub(db.Model):
    __tablename__ = 'smart_hubs'
    onenet_id = db.Column(db.Unicode(64, collation='utf8_bin'), primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('smart_users.id'))
    name = db.Column(db.Unicode(64, collation='utf8_bin'), server_default='插座', default='插座')
    mac = db.Column(db.Unicode(64, collation='utf8_bin'), nullable=False)
    timers = db.relationship('Timer', backref='Hub', lazy='dynamic', cascade='all, delete-orphan')

    def to_json(self):
        json = {
            'onenet_id': self.onenet_id,
            'name': self.name,
            'mac': self.mac,
        }
        return json

    def __repr__(self):
        return '<Hub(name={})>'.format(self.name)


class Device(db.Model):
    __tablename__ = 'smart_devices'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'), db.ForeignKey('smart_hubs.onenet_id'))
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    img = db.Column(db.Unicode(256, collation='utf8_bin'))
    eigenvalue = db.Column(db.Unicode(128, collation='utf8_bin'), nullable=False)

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
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'), db.ForeignKey('smart_hubs.onenet_id'))
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    # 0: 关 1: 开
    power = db.Column(db.SmallInteger, server_default='0', default='0', nullable=False)
    # 每天|每周1-5|一次性
    repeat = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    # 15:26
    time = db.Column(db.Unicode(32, collation='utf8_bin'), nullable=False)
    # 0: 关 1: 开
    status = db.Column(db.SmallInteger, server_default='0', default='0', nullable=False)

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
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'), db.ForeignKey('smart_hubs.onenet_id'))
    # 电价，暂时以广东为主
    price = db.Column(db.Float, server_default='0.6', default='0.6', nullable=False)
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
    hub_id = db.Column(db.Unicode(64, collation='utf8_bin'), db.ForeignKey('smart_hubs.onenet_id'))
    zero = db.Column(db.Float, server_default='0.0', default='0.0')
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

    def to_list(self):
        l = [
            self.zero, self.one, self.two, self.three, self.four, self.five, self.six, self.seven, self.eight,
            self.nine, self.ten, self.eleven, self.twelve, self.thirteen, self.fourteen, self.fifteen, self.sixteen,
            self.seventeen, self.eighteen, self.nineteen, self.twenty, self.twenty_one, self.twenty_two,
            self.twenty_three
        ]
        return l

    def __repr__(self):
        return '<HourSpare(month_id={})>'.format(self.month_id)
