#!/usr/bin/env python3
# -*- coding: utf-8 -*-
from flask import jsonify
from qiniu import Auth
from qiniu import CdnManager

from . import decorators

# 需要填写你的 Access Key 和 Secret Key
access_key = 'E-UOGVjHSKB59V8otdVUKHZtePBpW3jOJZH7SCbK'
secret_key = 'cUfgICuOfZ5-WmOXg2O1h1TxQrdwUbHnn4XrS2bP'


@decorators.route('/api/qiniu/<key>', methods=['GET'])
def get_qiniu_token(key):
    # 构建鉴权对象
    q = Auth(access_key, secret_key)
    # 要上传的空间
    bucket_name = 'smarthub'
    # 上传到七牛后保存的文件名key
    # 生成上传 Token，可以指定过期时间等
    token = q.upload_token(bucket_name, key, 3600)
    return jsonify({'msg': 'ok', 'result': token})


def refresh_cdn(urls):
    auth = Auth(access_key=access_key, secret_key=secret_key)
    cdn_manager = CdnManager(auth)
    # 刷新链接
    cdn_manager.refresh_urls(urls)
