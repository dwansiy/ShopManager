package com.xema.shopmanager.comparator;

import com.xema.shopmanager.model.Person;

import java.util.Comparator;

/**
 * Created by xema0 on 2018-07-02.
 */

//내림차순(최근이 제일 위로로)
public class PersonCreateComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
    }
}