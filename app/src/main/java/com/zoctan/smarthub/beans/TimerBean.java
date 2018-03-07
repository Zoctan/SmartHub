package com.zoctan.smarthub.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class TimerBean implements Serializable {
    private int id;
    private String hub_id;
    private String name;
    private int power;
    private String repeat;
    private String time;
    private int status;

    private String action;
}
