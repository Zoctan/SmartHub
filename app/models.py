#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import current_app
from app import db
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import (
    TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)


class User(db.Model):
    __tablename__ = 'smart_users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Unicode(32, collation='utf8_bin'), nullable=False, unique=True, index=True)
    password_hash = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    avatar = db.Column(db.Text)
    hubs = db.relationship('Hub', backref='User', lazy='dynamic', cascade='all, delete-orphan')

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
        user = User.query.get(data['id'])
        return user

    def to_json(self):
        json = {
            'id': self.id,
            'username': self.username,
            'avatar': self.avatar
        }
        return json


class Hub(db.Model):
    __tablename__ = 'smart_hubs'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Unicode(32, collation='utf8_bin'), default='排插')
    mac = db.Column(db.Unicode(64, collation='utf8_bin'), nullable=False)
    onenet_id = db.Column(db.Integer)
    eigenvalue = db.Column(db.Text)
    user = db.relationship('User', backref='Hub', uselist=False, lazy='select')
    timers = db.relationship('Timer', backref='Hub', lazy='dynamic', cascade='all, delete-orphan')
    spare = db.relationship('Spare', backref='Hub', uselist=False, lazy='select')

    def to_json(self):
        json = {
            'id': self.id,
            'onenet_id': self.onenet_id,
            'name': self.name,
            'mac': self.mac,
            'eigenvalue': self.eigenvalue
        }
        return json

    def __repr__(self):
        return '<Hub(name={})>'.format(self.name)


class Spare(db.Model):
    __tablename__ = 'smart_spares'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Integer, db.ForeignKey('smart_hubs.id'))
    hours = db.Column(db.Text)
    days = db.Column(db.Text)
    weeks = db.Column(db.Text)
    months = db.Column(db.Text)
    years = db.Column(db.Text)

    def to_json(self):
        # 00:00 v a|...|23:00 v a
        # 7.1 v a|...|7.2 v a
        json = {
            'id': self.id,
            'hours': self.hours.split('|'),
            'days': self.days.split('|'),
            'weeks': self.weeks.split('|'),
            'months': self.months.split('|'),
            'years': self.years.split('|')
        }
        return json


class Timer(db.Model):
    __tablename__ = 'smart_timers'
    id = db.Column(db.Integer, primary_key=True)
    hub_id = db.Column(db.Integer, db.ForeignKey('smart_hubs.id'))
    name = db.Column(db.Unicode(32, collation='utf8_bin'))
    task = db.Column(db.Unicode(256, collation='utf8_bin'))

    def to_json(self):
        json = {
            'id': self.id,
            'name': self.name,
            'task': self.task
        }
        return json


class Device(db.Model):
    __tablename__ = 'smart_devices'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    img = db.Column(db.Text)
    eigenvalue = db.Column(db.Text)

    def to_json(self):
        json = {
            'id': self.id,
            'name': self.name,
            'img': self.img
        }
        return json

    def __repr__(self):
        return '<Device(name={}, eigenvalue={})>'.format(self.name, self.eigenvalue)
