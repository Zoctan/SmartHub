package com.zoctan.smarthub.beans;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class OneNetDataStreamsBean implements Serializable {
    private String id;
    private List<String> tags;
    private String unit;
    private String unit_symbol;
    private String create_time;
    private String current_value;
    private String update_at;
}
