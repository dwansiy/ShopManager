package com.xema.shopmanager.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by xema0 on 2018-02-24.
 */

public class Chart implements Parent<Purchase> {
    private Date date;
    private List<Purchase> purchases;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    @Override
    public List<Purchase> getChildList() {
        return purchases != null ? purchases : new RealmList<>();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
