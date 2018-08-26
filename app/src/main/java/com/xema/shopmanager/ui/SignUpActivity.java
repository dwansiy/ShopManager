package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.enums.BusinessType;
import com.xema.shopmanager.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-05-15.
 */

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tv_beauty)
    TextView tvBeauty;
    @BindView(R.id.tv_business)
    TextView tvBusiness;
    @BindView(R.id.tv_lesson)
    TextView tvLesson;
    @BindView(R.id.tv_health)
    TextView tvHealth;
    @BindView(R.id.tv_education)
    TextView tvEducation;
    @BindView(R.id.tv_etc)
    TextView tvEtc;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.edt_business_name)
    EditText edtBusinessName;

    private BusinessType type = null;

    //private Realm realm;
    //private RealmAsyncTask transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
    }

    //@Override
    //protected void onStop() {
    //    if (transaction != null && !transaction.isCancelled()) {
    //        transaction.cancel();
    //    }
    //    super.onStop();
    //}

    //@Override
    //protected void onDestroy() {
    //    super.onDestroy();
    //    if (realm != null) {
    //        realm.close();
    //        realm = null;
    //    }
    //}

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListeners() {
        tvBeauty.setOnClickListener(this::attemptCheck);
        tvBusiness.setOnClickListener(this::attemptCheck);
        tvLesson.setOnClickListener(this::attemptCheck);
        tvHealth.setOnClickListener(this::attemptCheck);
        tvEducation.setOnClickListener(this::attemptCheck);
        tvEtc.setOnClickListener(this::attemptCheck);

        edtBusinessName.addTextChangedListener(new TextWatcher() {
            private final int MAX_LENGTH = 20;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCount.setText(getString(R.string.format_count_text, s.length(), MAX_LENGTH));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ivDone.setOnClickListener(this::attemptDone);
    }

    private void attemptCheck(View v) {
        resetCategoryViews();
        v.setSelected(true);
        if (v instanceof TextView)
            ((TextView) v).setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.colorGray1));

        switch (v.getId()) {
            case R.id.tv_beauty:
                type = BusinessType.BEAUTY;
                break;
            case R.id.tv_business:
                type = BusinessType.BUSINESS;
                break;
            case R.id.tv_lesson:
                type = BusinessType.LESSON;
                break;
            case R.id.tv_health:
                type = BusinessType.HEALTH;
                break;
            case R.id.tv_education:
                type = BusinessType.EDUCATION;
                break;
            case R.id.tv_etc:
                type = BusinessType.ETC;
                break;
            default:
                Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCategoryViews() {
        tvBeauty.setSelected(false);
        tvBeauty.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
        tvBusiness.setSelected(false);
        tvBusiness.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
        tvLesson.setSelected(false);
        tvLesson.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
        tvHealth.setSelected(false);
        tvHealth.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
        tvEducation.setSelected(false);
        tvEducation.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
        tvEtc.setSelected(false);
        tvEtc.setTextColor(ContextCompat.getColor(this, R.color.colorGray4));
    }

    private void attemptDone(View view) {
        if (type == null) {
            Snackbar.make(view, getString(R.string.message_error_not_select_business_type), Snackbar.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);

        User user = PreferenceHelper.loadUser(this);
        if (user == null) {
            view.setEnabled(true);
            Toast.makeText(SignUpActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            return;
        }
        user.setBusinessType(type);
        String businessName = edtBusinessName.getText().toString();
        if (!TextUtils.isEmpty(businessName)) {
            user.setBusinessName(businessName);
        }
        PreferenceHelper.saveUser(this, user);
        view.setEnabled(true);
        final Intent intent = new Intent(SignUpActivity.this, CustomerActivity.class);
        startActivity(intent);
        finish();

        /*
        transaction = realm.executeTransactionAsync(bgRealm -> {
            final Profile profile = bgRealm.where(Profile.class).findFirst();
            if (profile == null) {
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                });
                return;
            }
            profile.setBusinessType(type);
            String businessName = edtBusinessName.getText().toString();
            if (!TextUtils.isEmpty(businessName)) {
                profile.setBusinessName(businessName);
            }
            bgRealm.copyToRealmOrUpdate(profile);
        }, () -> {
            view.setEnabled(true);
            final Intent intent = new Intent(SignUpActivity.this, CustomerActivity.class);
            startActivity(intent);
            finish();
        }, error -> {
            //error.printStackTrace();
            view.setEnabled(true);
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        });
        */
    }
}
