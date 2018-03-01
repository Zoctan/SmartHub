package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

public class MonthSpareBean implements Serializable {
    private String id;
    private String hub_id;
    private float price;
    private float watt;
    private int current_month;
    private List<Float> hour;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getHub_id() {
        return hub_id;
    }

    public void setHub_id(final String hub_id) {
        this.hub_id = hub_id;
    }

    public int getCurrent_month() {
        return current_month;
    }

    public void setCurrent_month(final int current_month) {
        this.current_month = current_month;
    }

    public float getWatt() {
        return watt;
    }

    public void setWatt(final float watt) {
        this.watt = watt;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(final float price) {
        this.price = price;
    }

    public List<Float> getHour() {
        return hour;
    }

    public void setHour(final List<Float> hour) {
        this.hour = hour;
    }
}