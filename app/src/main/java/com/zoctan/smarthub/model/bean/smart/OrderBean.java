package com.zoctan.smarthub.model.bean.smart;

public class OrderBean {
    private String onenet_id;
    private String order;
    private int status;

    public OrderBean(final String onenet_id, final String order, final int status) {
        this.onenet_id = onenet_id;
        this.order = order;
        this.status = status;
    }

    public String getOnenet_id() {
        return onenet_id;
    }

    public void setOnenet_id(final String onenet_id) {
        this.onenet_id = onenet_id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(final String order) {
        this.order = order;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
