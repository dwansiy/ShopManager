package com.xema.shopmanager.model.wrapper;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Purchase;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by xema0 on 2018-02-24.
 */

// TODO: 2018-08-09 need refactoring
public class CategoryWrapper implements Parent<Purchase> {
    private Category category;
    private RealmList<Purchase> purchases = new RealmList<>();

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RealmList<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(RealmList<Purchase> purchases) {
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
