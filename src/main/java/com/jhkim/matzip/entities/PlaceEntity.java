package com.jhkim.matzip.entities;

import java.util.Date;
import java.util.Objects;

public class PlaceEntity {

    private int index;
    private String name;
    private String contactFirst;
    private String contactSecond;
    private String contactThird;
    private String description;
    private double latitude;
    private double longitude;
    private String addressPrimary;
    private String addressSecondary;
    private byte[] thumbnail;
    private String thumbnailMime;
    private String time;
    private String registeredBy;
    private Date registeredAt;
    private boolean isDeleted;

    public int getIndex() {
        return index;
    }

    public PlaceEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlaceEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getContactFirst() {
        return contactFirst;
    }

    public PlaceEntity setContactFirst(String contactFirst) {
        this.contactFirst = contactFirst;
        return this;
    }

    public String getContactSecond() {
        return contactSecond;
    }

    public PlaceEntity setContactSecond(String contactSecond) {
        this.contactSecond = contactSecond;
        return this;
    }

    public String getContactThird() {
        return contactThird;
    }

    public PlaceEntity setContactThird(String contactThird) {
        this.contactThird = contactThird;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PlaceEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public PlaceEntity setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public PlaceEntity setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getAddressPrimary() {
        return addressPrimary;
    }

    public PlaceEntity setAddressPrimary(String addressPrimary) {
        this.addressPrimary = addressPrimary;
        return this;
    }

    public String getAddressSecondary() {
        return addressSecondary;
    }

    public PlaceEntity setAddressSecondary(String addressSecondary) {
        this.addressSecondary = addressSecondary;
        return this;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public PlaceEntity setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public String getThumbnailMime() {
        return thumbnailMime;
    }

    public PlaceEntity setThumbnailMime(String thumbnail_mime) {
        this.thumbnailMime = thumbnail_mime;
        return this;
    }

    public String getTime() {
        return time;
    }

    public PlaceEntity setTime(String time) {
        this.time = time;
        return this;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public PlaceEntity setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
        return this;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public PlaceEntity setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public PlaceEntity setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceEntity that = (PlaceEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
