package com.zoctan.smarthub.model.bean.smart;

public class TimerBean {
    private int id;
    private String hub_id;
    private String name;
    private int power;
    private String repeat;
    private String time;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getHub_id() {
        return hub_id;
    }

    public void setHub_id(final String hub_id) {
        this.hub_id = hub_id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(final int power) {
        this.power = power;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(final String repeat) {
        this.repeat = repeat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
