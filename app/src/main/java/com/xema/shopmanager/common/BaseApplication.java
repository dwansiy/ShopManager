package com.xema.shopmanager.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.xema.shopmanager.model.User;

import java.util.function.BiConsumer;

import io.fabric.sdk.android.Fabric;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static com.xema.shopmanager.common.Constants.AUTH_URL;
import static com.xema.shopmanager.common.Constants.REALM_BASE_URL;

/**
 * Created by xema0 on 2018-02-10.
 */

public class BaseApplication extends Application {
    public static boolean DEBUG = false;

    // TODO: 2018-05-15 leak 제거
    private static Context context;

    private static class KakaoSDKAdapter extends KakaoAdapter {
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_TALK};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return BaseApplication::getGlobalApplicationContext;
        }
    }

    //todo
    public static Context getGlobalApplicationContext() {
        return context;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Fabric.with(this, new Crashlytics());

        KakaoSDK.init(new KakaoSDKAdapter());

        //스키마 변경시마다 버전 올리기 (https://realm.io/docs/java/latest/#migrations)
        Realm.init(this);
        //RealmConfiguration config = new RealmConfiguration.Builder()
        //        .schemaVersion(0)
        //        .deleteRealmIfMigrationNeeded()
        //        .migration(new SchemaMigration())
        //        .build();
        //Realm.setDefaultConfiguration(config);

        // TODO: 2018-08-17 카카오 로그인하고나서 sync 하도록 수정
        SyncUser syncUser = SyncUser.current();
        if (syncUser != null) {
            SyncConfiguration configuration = syncUser.createConfiguration(REALM_BASE_URL + "/" + syncUser.getIdentity()).build();
            Realm.setDefaultConfiguration(configuration);
        } else {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .schemaVersion(0)
                    .deleteRealmIfMigrationNeeded()
                    .migration(new SchemaMigration())
                    .build();
            Realm.setDefaultConfiguration(config);
        }

        //벡터 이미지 활성화
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        DEBUG = isDebuggable(this);
        if (DEBUG)
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
    }

    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }

        return debuggable;
    }
}
