package com.jhkim.matzip.entities;

import java.util.Date;
import java.util.Objects;

public class RegisterEmailCodeEntity {
    private int index;
    private String email;
    private String code;
    private String salt;
    private Date createdAt;
    private Date expiresAt;
    private boolean isExpired;

    public int getIndex() {
        return index;
    }

    public RegisterEmailCodeEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegisterEmailCodeEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RegisterEmailCodeEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public RegisterEmailCodeEntity setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RegisterEmailCodeEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RegisterEmailCodeEntity setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public RegisterEmailCodeEntity setExpired(boolean expired) {
        isExpired = expired;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterEmailCodeEntity that = (RegisterEmailCodeEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
