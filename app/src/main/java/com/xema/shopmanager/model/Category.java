package com.xema.shopmanager.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-02-19.
 */

public class Category extends RealmObject implements Parent<Product> {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String name;
    private RealmList<Product> products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Product> getProducts() {
        return products;
    }

    public void setProducts(RealmList<Product> products) {
        this.products = products;
    }

    @Override
    public List<Product> getChildList() {
        return products != null ? products : new RealmList<>();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
