package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.model.User;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.xema.shopmanager.common.Constants.AUTH_URL;
import static com.xema.shopmanager.common.Constants.REALM_BASE_URL;

/**
 * Created by xema0 on 2018-02-10.
 */

public class SplashActivity extends AppCompatActivity {
    //private Realm realm;

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        mHandler.postDelayed(mStartRunnable, 100);
    }

    private User getCurrentUser() {
        //if (realm == null) return null;
        //return realm.where(Profile.class).findFirst();
        return PreferenceHelper.loadUser(this);
    }

    private Runnable mStartRunnable = () -> {
        //realm = Realm.getDefaultInstance();

        User user = getCurrentUser();

        if (user == null) {
            //프로필 없을때
            final Intent intent = new Intent(SplashActivity.this, KakaoStartActivity.class);
            startActivity(intent);
            finish();
        } else if (user.getBusinessType() == null) {
            //프로필 있지만 업종 없을때
            final Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            //프로필 등록되어있을때
            final Intent intent = new Intent(SplashActivity.this, CustomerActivity.class);
            startActivity(intent);
            finish();

            // TODO: 2018-08-17  카카오 로그인 버튼 눌렀을때 원격 realm 에 있으면 동기화시켜줘야한다.
            /*
            if (user.isPremium()) {
                // TODO: 2018-08-17 프리미엄 등록했을때 앱을 재시작시켜서 이 경로로 실행시켜야함?!
                //자동동기화
                SyncCredentials credentials = SyncCredentials.nickname(String.valueOf(user.getKakaoId()), false);
                SyncUser.logInAsync(credentials, AUTH_URL, new SyncUser.Callback<SyncUser>() {
                    @Override
                    public void onSuccess(@NonNull SyncUser result) {
                        SyncConfiguration configuration = result.createConfiguration(REALM_BASE_URL + "/" + result.getIdentity()).build();
                        Realm.setDefaultConfiguration(configuration);

                        final Intent intent = new Intent(SplashActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ObjectServerError error) {
                        Toast.makeText(SplashActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                final Intent intent = new Intent(SplashActivity.this, CustomerActivity.class);
                startActivity(intent);
                finish();
            }
            */
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
