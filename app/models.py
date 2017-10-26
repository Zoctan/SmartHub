#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from app import db


class Device(db.Model):
    __tablename__ = 'smart_devices'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Unicode(256, collation='utf8_bin'), nullable=False)
    picture = db.Column(db.Text)
    crontab = db.Column(db.Text)

    @staticmethod
    def insert_default_data():
        names = ['插口' + str(i) for i in range(1, 5)]
        for i in names:
            name = Device.query.filter_by(name=i).first()
            if name is None:
                name = Device(name=i)
            db.session.add(name)
        db.session.commit()

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
