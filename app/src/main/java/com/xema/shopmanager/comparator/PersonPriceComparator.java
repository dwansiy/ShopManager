package com.xema.shopmanager.comparator;

import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;

import java.util.Comparator;

import io.realm.RealmList;

/**
 * Created by xema0 on 2018-07-02.
 */

// TODO: 2018-07-03 Need Refactoring
public class PersonPriceComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        long price1 = 0;
        RealmList<Sales> sales1 = o1.getSales();
        if (sales1 != null && sales1.size() != 0) {
            for (Sales sales : sales1) {
                RealmList<Purchase> purchases = sales.getPurchases();
                if (purchases != null && purchases.size() != 0) {
                    for (Purchase wrapper : purchases) {
                        price1 += wrapper.getCount() * wrapper.getProduct().getPrice();
                    }
                }
            }
        }

        long price2 = 0;
        RealmList<Sales> sales2 = o2.getSales();
        if (sales2 != null && sales2.size() != 0) {
            for (Sales sales : sales2) {
                RealmList<Purchase> purchases = sales.getPurchases();
                if (purchases != null && purchases.size() != 0) {
                    for (Purchase wrapper : purchases) {
                        price2 += wrapper.getCount() * wrapper.getProduct().getPrice();
                    }
                }
            }
        }

        return Long.compare(price2, price1);
    }
}