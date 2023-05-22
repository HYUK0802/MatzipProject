package com.jhkim.matzip.entities;

import java.util.Date;
import java.util.Objects;

public class RegisterContactCodeEntity {
    private int index;
    private String contact;
    private String code;
    private String salt;
    private Date createdAt;
    private Date expiresAt;
    private boolean isExpired;

    public int getIndex() {
        return index;
    }

    public RegisterContactCodeEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public RegisterContactCodeEntity setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RegisterContactCodeEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public RegisterContactCodeEntity setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RegisterContactCodeEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RegisterContactCodeEntity setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public RegisterContactCodeEntity setExpired(boolean expired) {
        isExpired = expired;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterContactCodeEntity that = (RegisterContactCodeEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
