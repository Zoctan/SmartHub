#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart


@smart.route('/crontabs', methods=['GET'])
def get_all_crontabs():
    return render_template('crontabs.html', id='crontabs', crontabs=[{'id': 'power_on',
                                                                      'name': '定时开机',
                                                                      'hour': '',
                                                                      'minute': '',
                                                                      'repeat': ''},
                                                                     {'id': 'power_off',
                                                                      'name': '定时关机',
                                                                      'hour': '',
                                                                      'minute': '',
                                                                      'repeat': ''}])
