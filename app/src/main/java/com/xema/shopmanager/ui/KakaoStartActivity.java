package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.xema.shopmanager.R;
import com.xema.shopmanager.common.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-05-15.
 */

public class KakaoStartActivity extends AppCompatActivity {
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;

    private SessionCallback callback;
    //private Realm realm;
    //private RealmAsyncTask transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_start);
        //realm = Realm.getDefaultInstance();

        ButterKnife.bind(this);

        btnSignIn.setOnClickListener(v -> {
            if (callback == null) {
                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
            }
            //Session.getCurrentSession().checkAndImplicitOpen();
            Session.getCurrentSession().open(AuthType.KAKAO_TALK, KakaoStartActivity.this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    @Override
    protected void onStop() {
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }
        super.onStop();
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        //if (realm != null) {
        //    realm.close();
        //    realm = null;
        //}
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                //Logger.e(exception);
                Toast.makeText(KakaoStartActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void redirectSignupActivity() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //Toast.makeText(KakaoStartActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                attemptRegister(result.getId(), result.getNickname(), result.getProfileImagePath());
            }
        });
    }


    private void attemptRegister(long kakaoId, String name, String profileImage) {
        com.xema.shopmanager.model.User user = new com.xema.shopmanager.model.User();
        user.setKakaoId(kakaoId);
        user.setName(name);
        user.setProfileImage(profileImage);
        PreferenceHelper.saveUser(this, user);
        final Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();

        /*
        if (realm == null) return;

        final Profile profile = new Profile();
        profile.setKakaoId(kakaoId);
        profile.setName(name);
        profile.setProfileImage(profileImage);

        transaction = realm.executeTransactionAsync(bgRealm -> {
            bgRealm.copyToRealmOrUpdate(profile);
        }, () -> {
            final Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }, error -> {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        });
    */
    }
}
