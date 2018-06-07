package com.xema.shopmanager.model;


import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-02-24.
 */

public class Purchase extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Product product;
    private int count;

    @LinkingObjects("purchases")
    private final RealmResults<Sales> sales = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public RealmResults<Sales> getSales() {
        return sales;
    }
}
