package com.xema.shopmanager.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.xema.shopmanager.model.wrapper.ProductWrapper;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by xema0 on 2018-02-24.
 */

public class Chart implements Parent<ProductWrapper> {
    private Date date;
    private RealmList<ProductWrapper> productWrappers = new RealmList<>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<ProductWrapper> getProductWrappers() {
        return productWrappers;
    }

    public void setProductWrappers(RealmList<ProductWrapper> productWrappers) {
        this.productWrappers = productWrappers;
    }

    @Override
    public List<ProductWrapper> getChildList() {
        return productWrappers != null ? productWrappers : new RealmList<>();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
