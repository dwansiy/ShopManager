package com.xema.shopmanager.comparator;

import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.utils.RealmUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by xema0 on 2018-07-02.
 */

public class PersonRecentComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        Date date1 = RealmUtils.getMaxSelectedDate(o1.getSales());
        Date date2 = RealmUtils.getMaxSelectedDate(o2.getSales());

        return date2 == null ? (date1 == null ? 0 : Integer.MIN_VALUE) : (date1 == null ? Integer.MAX_VALUE : date2.compareTo(date1));
    }
}
