package com.xema.shopmanager.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-05-15.
 */

public class Profile extends RealmObject {
    public enum BusinessType {
        BEAUTY, BUSINESS, LESSON, HEALTH, EDUCATION, ETC
    }

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private long kakaoId;
    private String name;
    private Date createdAt = new Date();
    private String profileImage;
    private String businessType;
    private String businessName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(long kakaoId) {
        this.kakaoId = kakaoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public BusinessType getBusinessType() {
        return BusinessType.valueOf(businessType);
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType.toString();
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
