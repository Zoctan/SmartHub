package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class OneNetDataPointBean implements Serializable {
    private String at;
    private float value;

    public String getHour(){
        String[] atArray = this.at.split("\\s+");
        return atArray[1].substring(0, 5);
    }

    public String getAt() {
        return this.at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}