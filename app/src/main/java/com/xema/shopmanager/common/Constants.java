package com.xema.shopmanager.common;

/**
 * Created by xema0 on 2018-02-17.
 */

public class Constants {
    public static final int REQUEST_CODE_ADD_CUSTOMER = 300;
    public static final int REQUEST_CODE_EDIT_CUSTOMER = 301;
    public static final int REQUEST_CODE_ADD_SALES = 310;
    public static final int REQUEST_CODE_EDIT_SALES = 311;
    public static final int REQUEST_CODE_CATEGORY = 320;

    public enum Sort {
        NAME, RECENT, PRICE, CREATE, VISIT;

        public static Sort toSort(String string) {
            try {
                return valueOf(string);
            } catch (Exception ex) {
                return NAME;
            }
        }
    }
}
