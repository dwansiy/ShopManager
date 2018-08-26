package com.xema.shopmanager.model;

import android.text.TextUtils;

import com.xema.shopmanager.enums.BusinessType;

import java.util.Date;
import java.util.UUID;

/**
 * Created by xema0 on 2018-08-17.
 */

public class User {
    private String id = UUID.randomUUID().toString();
    private long kakaoId;
    private String name;
    private Date createdAt = new Date();
    private String profileImage;
    private String businessType;
    private String businessName;
    // TODO: 2018-08-17 false 로 바꾸기
    private boolean premium = true;

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
        if (TextUtils.isEmpty(businessType)) return null;
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

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
