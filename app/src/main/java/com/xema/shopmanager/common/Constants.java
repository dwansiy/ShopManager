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
    public static final int REQUEST_CODE_EDIT_PROFILE_SETTING = 330;
    public static final int REQUEST_CODE_OPEN_GALLERY = 340;
    public static final int REQUEST_CODE_CROP_IMAGE = 341;
    public static final int REQUEST_CODE_SETTING = 350;
    public static final int REQUEST_CODE_SELECT_FILE = 360;

    private static final String REALM_INSTANCE_ADDRESS = "shopmanagertest.us1a.cloud.realm.io";
    public static final String AUTH_URL = "https://" + REALM_INSTANCE_ADDRESS + "/auth";
    public static final String REALM_BASE_URL = "realms://" + REALM_INSTANCE_ADDRESS;
}
