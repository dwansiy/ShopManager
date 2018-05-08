package com.xema.shopmanager.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xema0 on 2018-02-17.
 */

public class CommonUtil {
    public static String toDecimalFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static String toDecimalFormat(long num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static String getModifiedDate(Date modified) {
        return getModifiedDate(Locale.getDefault(), modified);
    }

    private static String getModifiedDate(Locale locale, Date modified) {
        SimpleDateFormat dateFormat = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dateFormat = new SimpleDateFormat(getDateFormat(locale), locale);
        } else {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy", locale);
        }

        return dateFormat.format(modified);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static String getDateFormat(Locale locale) {
        //return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy hh:mm:ss aa");
        return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy");
    }

    // TODO: 2018-02-25 시간을 선택하는 화면이 없으니 그냥 날짜만..
    //public static String getModifiedLongDate(Date modified) {
    //    return getModifiedDate(Locale.getDefault(), modified);
    //}
//
    //private static String getModifiedLongDate(Locale locale, Date modified) {
    //    SimpleDateFormat dateFormat = null;
//
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
    //        dateFormat = new SimpleDateFormat(getLongDateFormat(locale), locale);
    //    } else {
    //        dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", locale);
    //    }
//
    //    return dateFormat.format(modified);
    //}
//
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    //private static String getLongDateFormat(Locale locale) {
    //    return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy hh:mm:ss aa");
    //}

    public static String toHypenFormat(String str) {
        if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) return str;

        StringBuilder builder = new StringBuilder();
        if (str.length() < 4) {
            return str;
        } else if (str.length() < 7) {
            builder.append(str.substring(0, 3));
            builder.append('-');
            builder.append(str.substring(3));
            return builder.toString();
        } else if (str.length() < 11) {
            builder.append(str.substring(0, 3));
            builder.append('-');
            builder.append(str.substring(3, 6));
            builder.append('-');
            builder.append(str.substring(6));
            return builder.toString();
        } else {
            builder.append(str.substring(0, 3));
            builder.append('-');
            builder.append(str.substring(3, 7));
            builder.append('-');
            builder.append(str.substring(7));
            return builder.toString();
        }
    }

    public static void animRotate(View view, float start, float end) {
        RotateAnimation rotate = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setInterpolator(new DecelerateInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
