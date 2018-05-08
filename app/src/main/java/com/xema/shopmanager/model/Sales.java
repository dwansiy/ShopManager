package com.xema.shopmanager.model;

import com.xema.shopmanager.model.wrapper.ProductWrapper;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-02-24.
 */

public class Sales extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Date createdAt = new Date();
    private Date selectedAt;
    private RealmList<ProductWrapper> productWrappers = new RealmList<>();
    private String memo;

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSelectedAt() {
        return selectedAt;
    }

    public void setSelectedAt(Date selectedAt) {
        this.selectedAt = selectedAt;
    }

    public RealmList<ProductWrapper> getProductWrappers() {
        return productWrappers;
    }

    public void setProductWrappers(RealmList<ProductWrapper> productWrappers) {
        this.productWrappers = productWrappers;
    }
}
