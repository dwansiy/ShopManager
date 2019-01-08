package com.xema.shopmanager.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.enums.BusinessType;
import com.xema.shopmanager.model.User;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;
import com.xema.shopmanager.utils.PermissionUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.SyncUser;

/**
 * Created by xema0 on 2018-07-24.
 */

public class ProfileSettingActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.riv_profile)
    RoundedImageView rivProfile;
    @BindView(R.id.edt_name)
    EditText edtName;
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
    @BindView(R.id.iv_edit_profile)
    ImageView ivEditProfile;
    @BindView(R.id.btn_sign_out)
    Button btnSignOut;

    //private Realm realm;

    private User user;
    private BusinessType type = null;

    private File mProfileImageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        //realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        user = PreferenceHelper.loadUser(this);
        if (user == null) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initUserData(user);
    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
    */

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
        ivBack.setOnClickListener(v -> finish());
        ivEditProfile.setOnClickListener(this::attemptEditProfileImage);

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


        btnSignOut.setOnClickListener(v -> {
            SimpleTextDialog dialog = new SimpleTextDialog(this, getString(R.string.message_alert_sign_out));
            dialog.setOnPositiveListener(getString(R.string.action_sign_out), () -> {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                PreferenceHelper.resetAll(ProfileSettingActivity.this);
                Intent intent = new Intent(ProfileSettingActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            dialog.show();
        });
    }

    private void initUserData(User user) {
        edtName.setText(user.getName());
        edtBusinessName.setText(user.getBusinessName());
        GlideApp.with(this).load(user.getProfileImage()).into(rivProfile);

        type = user.getBusinessType();
        if (type == null) return;
        switch (type) {
            case BEAUTY:
                attemptCheck(tvBeauty);
                break;
            case BUSINESS:
                attemptCheck(tvBusiness);
                break;
            case LESSON:
                attemptCheck(tvLesson);
                break;
            case HEALTH:
                attemptCheck(tvHealth);
                break;
            case EDUCATION:
                attemptCheck(tvEducation);
                break;
            case ETC:
                attemptCheck(tvEtc);
                break;
        }
    }

    private void attemptCheck(View v) {
        resetCategoryViews();
        v.setSelected(true);
        if (v instanceof TextView)
            ((TextView) v).setTextColor(ContextCompat.getColor(this, R.color.colorGray1));

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

    private void attemptEditProfileImage(View v) {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startGallery(Constants.REQUEST_CODE_OPEN_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSION_GALLERY:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    startGallery(Constants.REQUEST_CODE_OPEN_GALLERY);
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
        }
    }

    private void startGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    private void startCrop(Uri originalUri, int requestCode) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.colorWhite));
        options.setToolbarWidgetColor(getResources().getColor(R.color.colorGray3));
        options.setToolbarTitle(getString(R.string.title_crop_image));
        options.setLogoColor(getResources().getColor(R.color.colorBlack));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorGray4));
        options.setStatusBarColor(getResources().getColor(R.color.colorBlack));
        options.setCompressionQuality(90);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        if (requestCode == Constants.REQUEST_CODE_OPEN_GALLERY) {
            mProfileImageFile = new File(this.getCacheDir(), System.currentTimeMillis() + "_crop");
            UCrop.of(originalUri, Uri.fromFile(mProfileImageFile)).withOptions(options).withAspectRatio(1, 1).withMaxResultSize(300, 300).start(this, Constants.REQUEST_CODE_CROP_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Constants.REQUEST_CODE_OPEN_GALLERY) && (resultCode == RESULT_OK) && data != null) {
            if (data.getData() != null) {
                startCrop(data.getData(), requestCode);
            } else {
                Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == Constants.REQUEST_CODE_CROP_IMAGE) && (resultCode == RESULT_OK)) {
            GlideApp.with(this).load(mProfileImageFile).into(rivProfile);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptDone(View view) {
        if (type == null) {
            Snackbar.make(view, getString(R.string.message_error_not_select_business_type), Snackbar.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);

        if (user == null) {
            Toast.makeText(ProfileSettingActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
            return;
        }

        user.setBusinessType(type);
        String name = edtName.getText().toString();
        user.setName(name);
        String businessName = edtBusinessName.getText().toString();
        user.setBusinessName(businessName);
        if (mProfileImageFile != null) {
            user.setProfileImage(mProfileImageFile.toString());
        }
        PreferenceHelper.saveUser(this, user);
        view.setEnabled(true);
        setResult(RESULT_OK);
        finish();

        /*
        realm.executeTransaction(realm -> {
            if (mProfile == null) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileSettingActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                });
                return;
            }
            mProfile.setBusinessType(type);
            String name = edtName.getText().toString();
            mProfile.setName(name);
            String businessName = edtBusinessName.getText().toString();
            mProfile.setBusinessName(businessName);
            if (mProfileImageFile != null) {
                mProfile.setProfileImage(mProfileImageFile.toString());
            }
            realm.copyToRealmOrUpdate(mProfile);

            view.setEnabled(true);
            setResult(RESULT_OK);
            finish();
        });
        */
    }
}
