#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import re


def is_found(_file, _word):
    with open(_file, 'r') as f:
        if re.findall(_word, f.read()):
            return True
    return False
