package com.zoctan.smarthub.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeviceBean implements Serializable {
    private Integer id;
    private String hub_id;
    private String name;
    private String img;
    private int eigenvalue;

    // 非数据库字段，只是为了方便数据操作
    private String action;
}
