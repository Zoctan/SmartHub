package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MonthSpareBean implements Serializable {
    private String id;
    private String hub_id;
    private float price;
    private float watt;
    private int current_month;
    private List<Float> hour;
}