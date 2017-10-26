#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart
from ..models import *


@smart.route('/', methods=['GET'])
def index():
    return render_template('index.html', id='status')


@smart.route('/app/init', methods=['GET'])
def init():
    db.drop_all()
    db.create_all()
    Device().insert_default_data()
    return 'well'
