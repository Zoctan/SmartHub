#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from app import db


class Device(db.Model):
    __tablename__ = 'smart_devices'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False, index=True)
    picture = db.Column(db.Text)
    crontab = db.Column(db.Text)

    def to_json(self):
        json = {
            'id': self.id,
            'name': self.name,
            'picture': self.picture,
            'crontab': self.crontab
        }
        return json

    def __repr__(self):
        return '<Device(title={})>'.format(self.name)
