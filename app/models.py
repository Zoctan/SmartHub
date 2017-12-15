#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import current_app
from app import db
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)


class User(db.Model):
    __tablename__ = 'smart_users'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.Unicode(32, collation='utf8_bin'), nullable=False, unique=True, index=True)
    password_hash = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    avatar = db.Column(db.Unicode(256, collation='utf8_bin'), default="http://p0qgwnuel.bkt.clouddn.com/default.png")
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


class AnonymousUser(User):
    pass


class Hub(db.Model):
    __tablename__ = 'smart_hubs'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('smart_users.id'))
    name = db.Column(db.Unicode(64, collation='utf8_bin'), default='插座')
    mac = db.Column(db.Unicode(64, collation='utf8_bin'), nullable=False)
    onenet_id = db.Column(db.Unicode(64, collation='utf8_bin'))

    def to_json(self):
        json = {
            'id': self.id,
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
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    img = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    eigenvalue = db.Column(db.Unicode(128, collation='utf8_bin'), nullable=False)

    def to_json(self):
        json = {
            'id': self.id,
            'name': self.name,
            'img': self.img,
            'eigenvalue': self.eigenvalue
        }
        return json

    def __repr__(self):
        return '<Device(name={}, eigenvalue={})>'.format(self.name, self.eigenvalue)
