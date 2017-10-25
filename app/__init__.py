#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from config import config
from werkzeug.contrib.fixers import ProxyFix

db = SQLAlchemy()


def create_app(config_name):
    app = Flask(__name__)
    CORS(app)
    app.config.from_object(config[config_name])
    app.wsgi_app = ProxyFix(app.wsgi_app)
    config[config_name].init_app(app)
    db.init_app(app)
    from .smart_1_0 import smart as smart_blueprint
    app.register_blueprint(smart_blueprint)
    return app
