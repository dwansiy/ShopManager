package com.xema.shopmanager.utils;

import com.xema.shopmanager.model.Sales;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by xema0 on 2018-07-02.
 */

public class RealmUtils {
    //비 관리 리스트일때 maxDate() 대신 사용
    public static Date getMaxSelectedDate(List<Sales> salesList) {
        if (salesList == null || salesList.size() == 0) return null;

        return Collections.max(salesList, (o1, o2) -> {
            Date date1 = o1.getSelectedAt();
            Date date2 = o2.getSelectedAt();

            if (date1 == null && date2 == null) return 0;
            if (date1 == null) return 1;
            if (date2 == null) return -1;

            return date2.compareTo(date1);
        }).getSelectedAt();
    }
}
