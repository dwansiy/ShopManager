package com.xema.shopmanager.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xema.shopmanager.BuildConfig;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Profile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

import static android.content.ContentValues.TAG;
import static com.kakao.util.helper.Utility.getPackageInfo;

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

    public static void focusLastCharacter(EditText editText) {
        int pos = editText.getText().length();
        editText.setSelection(pos);
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

    public static void showKeyBoard(Context context, EditText editText) {
        editText.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.showSoftInput(editText, 0);
            }
        }, 50);
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    public static List<Person> filterByNameAndPhone(List<Person> originalList, String searchText) {
        if (originalList == null || originalList.isEmpty()) return null;

        List<Person> filteredList = new ArrayList<>();
        for (Person person : originalList) {
            String name = person.getName();
            String phone = person.getPhone();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                if (name.contains(searchText)) {
                    filteredList.add(person);
                } else if (phone.contains(searchText)) {
                    filteredList.add(person);
                } else if (InitialSoundUtil.matchString(name, searchText)) {
                    filteredList.add(person);
                }
            } else if (!TextUtils.isEmpty(name)) {
                if (name.contains(searchText)) {
                    filteredList.add(person);
                } else if (InitialSoundUtil.matchString(name, searchText)) {
                    filteredList.add(person);
                }
            } else if (!TextUtils.isEmpty(phone)) {
                if (phone.contains(searchText)) {
                    filteredList.add(person);
                }
            }
        }
        return filteredList;
    }

    public static String makeReportString(Context context, Realm realm) {
        String bodyString;
        if (realm != null && !realm.isClosed()) {
            Profile profile = realm.where(Profile.class).findFirst();
            if (profile != null) {
                bodyString = context.getString(R.string.report_body_long, getAndroidVersion(), getAppVersion(), profile.getId(), String.valueOf(profile.getKakaoId()), profile.getName());
                return "mailto:xema027@gmail.com?" + "&body=" + Uri.encode(bodyString);
            }
        }
        bodyString = context.getString(R.string.report_body_short, getAndroidVersion(), getAppVersion());
        return "mailto:xema027@gmail.com?" + "&body=" + Uri.encode(bodyString);
    }

    private static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion + " (" + release + ")";
    }

    private static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
