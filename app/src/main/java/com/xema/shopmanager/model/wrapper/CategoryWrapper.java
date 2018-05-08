package com.xema.shopmanager.model.wrapper;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.xema.shopmanager.model.Category;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xema0 on 2018-02-24.
 */

public class CategoryWrapper implements Parent<ProductWrapper> {
    private Category category;
    private RealmList<ProductWrapper> productWrappers = new RealmList<>();

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
