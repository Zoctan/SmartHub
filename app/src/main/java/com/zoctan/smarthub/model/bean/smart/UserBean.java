package com.zoctan.smarthub.model.bean.smart;

public class UserBean {
    private String id;
    private String token;
    private String username;
    private String password;
    private String avatar;
    private String phone;
    private String email;

    public UserBean() {
    }

    private UserBean(final Builder builder) {
        setId(builder.id);
        setToken(builder.token);
        setUsername(builder.username);
        setPassword(builder.password);
        setAvatar(builder.avatar);
        setPhone(builder.phone);
        setEmail(builder.email);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
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
        private String id;
        private String token;
        private String username;
        private String password;
        private String avatar;
        private String phone;
        private String email;

        public Builder() {
        }

        public Builder id(final String val) {
            id = val;
            return this;
        }

        public Builder token(final String val) {
            token = val;
            return this;
        }

        public Builder username(final String val) {
            username = val;
            return this;
        }

        public Builder password(final String val) {
            password = val;
            return this;
        }

        public Builder avatar(final String val) {
            avatar = val;
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

        public UserBean build() {
            return new UserBean(this);
        }
    }
}
