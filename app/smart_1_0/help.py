#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart


@smart.route('/help', methods=['GET'])
def get_help():
    return render_template('help.html')
