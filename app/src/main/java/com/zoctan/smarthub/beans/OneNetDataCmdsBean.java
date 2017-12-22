package com.zoctan.smarthub.beans;

import java.io.Serializable;

public class OneNetDataCmdsBean implements Serializable {
    private String cmd_uuid;

    public String getCmd_uuid() {
        return cmd_uuid;
    }

    public void setCmd_uuid(String cmd_uuid) {
        this.cmd_uuid = cmd_uuid;
    }
}
