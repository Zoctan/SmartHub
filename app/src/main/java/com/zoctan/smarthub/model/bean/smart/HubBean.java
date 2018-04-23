package com.zoctan.smarthub.model.bean.smart;

public class HubBean {
    private String onenet_id;
    private String name;
    private String mac;
    private Boolean connected;
    private String room;
    private Boolean is_electric;
    private String eigenvalue;

    public HubBean() {
    }

    private HubBean(final Builder builder) {
        setOnenet_id(builder.onenet_id);
        setName(builder.name);
        setMac(builder.mac);
        setConnected(builder.connected);
        setIs_electric(builder.is_electric);
        setEigenvalue(builder.eigenvalue);
        setRoom(builder.room);
    }

    public String getOnenet_id() {
        return onenet_id;
    }

    public void setOnenet_id(final String onenet_id) {
        this.onenet_id = onenet_id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(final String mac) {
        this.mac = mac;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(final Boolean connected) {
        this.connected = connected;
    }

    public Boolean getIs_electric() {
        return is_electric;
    }

    public void setIs_electric(final Boolean is_electric) {
        this.is_electric = is_electric;
    }

    public String getEigenvalue() {
        return eigenvalue;
    }

    public void setEigenvalue(final String eigenvalue) {
        this.eigenvalue = eigenvalue;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(final String room) {
        this.room = room;
    }

    public static final class Builder {
        private String onenet_id;
        private String name;
        private String mac;
        private Boolean connected;
        private Boolean is_electric;
        private String room;
        private String eigenvalue;

        public Builder() {
        }

        public Builder onenet_id(final String val) {
            onenet_id = val;
            return this;
        }

        public Builder name(final String val) {
            name = val;
            return this;
        }

        public Builder mac(final String val) {
            mac = val;
            return this;
        }

        public Builder connected(final Boolean val) {
            connected = val;
            return this;
        }

        public Builder is_electric(final Boolean val) {
            is_electric = val;
            return this;
        }

        public Builder eigenvalue(final String val) {
            eigenvalue = val;
            return this;
        }

        public Builder room(final String val) {
            room = val;
            return this;
        }

        public HubBean build() {
            return new HubBean(this);
        }
    }
}
