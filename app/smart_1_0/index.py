#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart
from ..models import *


@smart.route('/', methods=['GET'])
def index():
    return render_template('index.html', id='status')
