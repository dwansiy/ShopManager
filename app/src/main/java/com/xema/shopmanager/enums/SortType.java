package com.xema.shopmanager.enums;

/**
 * Created by xema0 on 2018-08-17.
 */

public enum SortType {
    NAME, RECENT, PRICE, CREATE, VISIT;

    public static SortType toSortType(String string) {
        try {
            return valueOf(string);
        } catch (Exception ex) {
            return NAME;
        }
    }
}