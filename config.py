#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os


class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'the quick brown fox jumps over the lazy dog'
    SQLALCHEMY_TRACK_MODIFICATIONS = True

    @staticmethod
    def init_app(app):
        pass


class DevelopmentConfig(Config):
    DEBUG = True
    MAIL_DEBUG = True  # 开启debug，便于调试看信息
    MAIL_SUPPRESS_SEND = False  # 发送邮件，为True则不发送
    MAIL_SERVER = 'smtp.163.com'  # 邮箱服务器
    MAIL_PORT = 465  # 端口
    MAIL_USE_SSL = True  # 重要，qq邮箱需要使用SSL
    MAIL_USE_TLS = False  # 不需要使用TLS
    MAIL_USERNAME = 'InitSmartHub@163.com'  # 填邮箱
    MAIL_PASSWORD = 'wgh12369'  # 填授权码
    MAIL_DEFAULT_SENDER = 'InitSmartHub@163.com'  # 填邮箱，默认发送者
    SQLALCHEMY_DATABASE_URI = os.environ.get(
            'DEV_DATABASE_URL') or 'mysql+pymysql://root:root@127.0.0.1:3306/smart?charset=utf8'


class TestingConfig(Config):
    TESTING = True
    MAIL_DEBUG = True
    MAIL_SUPPRESS_SEND = False
    MAIL_SERVER = 'smtp.163.com'
    MAIL_PORT = 465
    MAIL_USE_SSL = True
    MAIL_USE_TLS = False
    MAIL_USERNAME = 'InitSmartHub@163.com'
    MAIL_PASSWORD = 'wgh12369'
    MAIL_DEFAULT_SENDER = 'InitSmartHub@163.com'
    SQLALCHEMY_DATABASE_URI = os.environ.get(
            'TEST_DATABASE_URL') or 'mysql+pymysql://root:root@127.0.0.1:3306/testsmart?charset=utf8'


class ProductionConfig(Config):
    MAIL_DEBUG = False
    MAIL_SUPPRESS_SEND = False
    MAIL_SERVER = 'smtp.163.com'
    MAIL_PORT = 465
    MAIL_USE_SSL = True
    MAIL_USE_TLS = False
    MAIL_USERNAME = 'InitSmartHub@163.com'
    MAIL_PASSWORD = 'wgh12369'
    MAIL_DEFAULT_SENDER = 'InitSmartHub@163.com'
    SQLALCHEMY_DATABASE_URI = os.environ.get(
            'DATABASE_URL') or 'mysql+pymysql://root:root@127.0.0.1:3306/smart?charset=utf8'


config = {
    'development': DevelopmentConfig,
    'testing': TestingConfig,
    'production': ProductionConfig
}
