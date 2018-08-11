package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Profile;

import io.realm.Realm;

/**
 * Created by xema0 on 2018-02-10.
 */

public class SplashActivity extends AppCompatActivity {
    private Realm realm;

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        mHandler.postDelayed(mStartRunnable, 100);
    }

    private Profile getMyProfile() {
        if (realm == null) return null;
        return realm.where(Profile.class).findFirst();
    }

    private Runnable mStartRunnable = () -> {
        realm = Realm.getDefaultInstance();

        Profile myProfile = getMyProfile();
        if (myProfile == null) {
            //프로필 없을때
            final Intent intent = new Intent(SplashActivity.this, KakaoStartActivity.class);
            startActivity(intent);
            finish();
        } else if (myProfile.getBusinessType() == null) {
            //프로필 있지만 업종 없을때
            final Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            //프로필 등록되어있을때
            final Intent intent = new Intent(this, CustomerActivity.class);
            startActivity(intent);
            finish();
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    };

    @Override
    public void finish() {
        if (mHandler != null)
            mHandler.removeCallbacks(mStartRunnable);
        super.finish();
    }
}
