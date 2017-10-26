#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import render_template
from . import smart


@smart.errorhandler(404)
def error_404(error):
    return render_template('404.html'), 404
