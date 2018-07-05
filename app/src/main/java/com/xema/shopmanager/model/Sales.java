package com.xema.shopmanager.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-02-24.
 */

public class Sales extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Date createdAt = new Date();
    private Date selectedAt;
    private RealmList<Purchase> purchases;
    private String memo;

    @LinkingObjects("sales")
    private final RealmResults<Person> person = null;

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

    public RealmList<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(RealmList<Purchase> purchases) {
        this.purchases = purchases;
    }

    public RealmResults<Person> getPerson() {
        return person;
    }

    public long getPrice() {
        long price = 0;
        for (Purchase purchase : getPurchases()) {
            price += purchase.getCount() * purchase.getProduct().getPrice();
        }
        return price;
    }
}
