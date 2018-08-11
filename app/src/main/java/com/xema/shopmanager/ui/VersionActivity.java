package com.xema.shopmanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.BuildConfig;
import com.xema.shopmanager.R;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-08-09.
 */

public class VersionActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tv_device_version)
    TextView tvDeviceVersion;
    @BindView(R.id.tv_market_version)
    TextView tvMarketVersion;
    @BindView(R.id.btn_update)
    Button btnUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);

        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack.setOnClickListener(v -> finish());
        tvDeviceVersion.setText(getReleaseVersionString());

        new VersionChecker(this).execute();
    }

    private void updateUI(String marketVersion) {
        if (TextUtils.isEmpty(marketVersion)) {
            btnUpdate.setText(getString(R.string.message_network_not_enabled));
            btnUpdate.setEnabled(true);
            btnUpdate.setOnClickListener(this::attemptUpdate);
            return;
        }

        String deviceVersion = getReleaseVersionString();
        if (needUpdate(deviceVersion, marketVersion)) {
            btnUpdate.setText(getString(R.string.action_update));
            btnUpdate.setEnabled(true);
            btnUpdate.setOnClickListener(this::attemptUpdate);
        } else {
            btnUpdate.setText(getString(R.string.message_version_latest));
            btnUpdate.setEnabled(false);
        }
    }

    private void attemptUpdate(View view) {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }

    //ex)1.0.0.0
    private static String getReleaseVersionString() {
        return BuildConfig.VERSION_NAME;
    }

    private static boolean needUpdate(String deviceVersion, String marketVersion) {
        try {
            String[] vals1 = deviceVersion.split("\\.");
            String[] vals2 = marketVersion.split("\\.");
            int i = 0;
            while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
                i++;
            }
            if (i < vals1.length && i < vals2.length) {
                int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
                return Integer.signum(diff) == -1;
            }
            return Integer.signum(vals1.length - vals2.length) == -1;
        } catch (Exception e) {
            return false;
        }
    }

    // TODO: 2018-08-09 마켓에 올리고 나서 잘되나 확인
    private static final class VersionChecker extends AsyncTask<Void, Void, String> {
        private final WeakReference<VersionActivity> ref;
        VersionChecker(VersionActivity ref) {
            this.ref = new WeakReference<>(ref);
        }
        @Override
        protected String doInBackground(Void... voids) {
            String marketVersion = null;
            try {
                marketVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.xema.shopmanager" + "&hl=en")
                        .timeout(20000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                //do nothing
            }
            return marketVersion;
        }
        @Override
        protected void onPostExecute(String marketVersion) {
            super.onPostExecute(marketVersion);
            VersionActivity activity = ref.get();
            if (activity == null) return;
            activity.updateUI(marketVersion);
        }
    }
}
