package com.zoctan.smarthub.model.bean.smart;

public class FeedbackBean {
    private String phone;
    private String email;
    private String msg;

    public FeedbackBean(final String email, final String phone, final String msg) {
        this.email = email;
        this.phone = phone;
        this.msg = msg;
    }

    private FeedbackBean(final Builder builder) {
        setMsg(builder.msg);
        setPhone(builder.phone);
        setEmail(builder.email);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public static final class Builder {
        private String msg;
        private String phone;
        private String email;

        public Builder() {
        }

        public Builder msg(final String val) {
            msg = val;
            return this;
        }

        public Builder phone(final String val) {
            phone = val;
            return this;
        }

        public Builder email(final String val) {
            email = val;
            return this;
        }

        public FeedbackBean build() {
            return new FeedbackBean(this);
        }
    }
}
