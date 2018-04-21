package com.zoctan.smarthub.model.bean.onenet;

import java.util.List;

public class OneNetDataStreamsBean {
    private String id;
    private List<String> tags;
    private String unit;
    private String unit_symbol;
    private String create_time;
    private Double current_value;
    private String update_at;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public String getUnit_symbol() {
        return unit_symbol;
    }

    public void setUnit_symbol(final String unit_symbol) {
        this.unit_symbol = unit_symbol;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(final String create_time) {
        this.create_time = create_time;
    }

    public Double getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(final Double current_value) {
        this.current_value = current_value;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(final String update_at) {
        this.update_at = update_at;
    }
}
