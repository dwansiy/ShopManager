package com.xema.shopmanager.comparator;

import com.xema.shopmanager.model.Person;

import java.util.Comparator;

/**
 * Created by xema0 on 2018-07-02.
 */

/* Managed List Must Use RealmQuery.sort() */
@Deprecated
public class PersonNameComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getName().compareTo(o2.getName());
    }
}