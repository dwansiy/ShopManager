package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xema.shopmanager.R;
import com.xema.shopmanager.utils.CommonUtil;

/**
 * Created by xema0 on 2018-02-10.
 */

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        mHandler.postDelayed(mStartRunnable, 10000);
    }

    private Runnable mStartRunnable = () -> {
        Intent intent = new Intent(SplashActivity.this, KakaoStartActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    public void finish() {
        mHandler.removeCallbacks(mStartRunnable);
        super.finish();
    }
}
