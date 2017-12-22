package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

public class OneNetDataStreamsBean implements Serializable {
    private String id;
    private List<String> tags;
    private String unit;
    private String unit_symbol;
    private String create_time;
    private String current_value;
    private String update_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit_symbol() {
        return unit_symbol;
    }

    public void setUnit_symbol(String unit_symbol) {
        this.unit_symbol = unit_symbol;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(String current_value) {
        this.current_value = current_value;
    }
}
