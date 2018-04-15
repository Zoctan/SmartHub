package com.zoctan.smarthub.model.bean.smart;

public class DeviceBean {
    private Integer id;
    private String hub_id;
    private String name;
    private String img;
    private int eigenvalue;

    public DeviceBean() {
    }

    public DeviceBean(final String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
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

    public String getImg() {
        return img;
    }

    public void setImg(final String img) {
        this.img = img;
    }

    public int getEigenvalue() {
        return eigenvalue;
    }

    public void setEigenvalue(final int eigenvalue) {
        this.eigenvalue = eigenvalue;
    }
}
