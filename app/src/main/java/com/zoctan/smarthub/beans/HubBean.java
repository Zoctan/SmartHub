package com.zoctan.smarthub.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class HubBean implements Serializable {
    private String onenet_id;
    private String name;
    private String mac;
    private Boolean connected;
    private Boolean is_electric;
    private String eigenvalue;

    private String action;
}
