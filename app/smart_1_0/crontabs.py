#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import request, render_template
from . import smart
from ..models import *


@smart.route('/crontabs', methods=['GET'])
def get_all_crontabs():
    return render_template('crontabs.html', id='crontabs')
