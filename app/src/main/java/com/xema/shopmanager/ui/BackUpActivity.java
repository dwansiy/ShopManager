package com.xema.shopmanager.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.ui.dialog.MakeBackUpFileDialog;
import com.xema.shopmanager.utils.PermissionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.internal.IOException;

/**
 * Created by xema0 on 2018-08-11.
 */

// TODO: 2018-08-11 권한 설정 및 백업파일 관리
public class BackUpActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.btn_make_back_up)
    Button btnMakeBackUp;
    @BindView(R.id.btn_export_back_up)
    Button btnExportBackUp;
    @BindView(R.id.btn_import_back_up)
    Button btnImportBackUp;
    @BindView(R.id.tv_message_path)
    TextView tvMessagePath;

    private Realm realm;
    private static final String DIRECTORY = Environment.getExternalStorageDirectory().getPath().concat("/Shop Manager");
    private static final String IMPORT_REALM_FILE_NAME = "default.realm";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        tvMessagePath.setText(getString(R.string.message_back_up_file_path, DIRECTORY));

        initToolbar();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnMakeBackUp.setOnClickListener(v -> attemptMakeBackUpFile());
        btnExportBackUp.setOnClickListener(v -> attemptExportBackUpFile());
        btnImportBackUp.setOnClickListener(v -> attemptImportBackUpFile());
    }

    private void attemptMakeBackUpFile() {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_MAKE_BACK_UP_FILE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showFileNameDialog();
        }
    }

    private void attemptExportBackUpFile() {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_EXPORT_BACK_UP_FILE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            exportBackUpFile();
        }
    }

    private void attemptImportBackUpFile() {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_IMPORT_BACK_UP_FILE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showFileChooser();
        }
    }

    private void showFileNameDialog() {
        String fileName = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()).concat("_backup");
        MakeBackUpFileDialog dialog = new MakeBackUpFileDialog(this, fileName);
        dialog.setListener(new MakeBackUpFileDialog.OnRegisterListener() {
            @Override
            public void onRegister(String name) {
                File file = makeBackUpFile(name);
                if (file == null) {
                    Toast.makeText(BackUpActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BackUpActivity.this, getString(R.string.message_make_back_up_file_complete, DIRECTORY), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    // TODO: 2018-08-09  csv 파일로 변환하는건 프리미엄 모델에서 해주는걸로. api가 없어서 수동으로 csv 파일 생성하는 코드 만들어줘야함
    // TODO: 2018-08-09 스키마 버전 고려하고, 프로필 데이터는 realm 에서 제외해야할듯(서로 다른 profile일 경우 카카오톡에서 꼬임... 아니면 카카오톡 로그인을 삭제하거나 해야할듯)
    private File makeBackUpFile(String fileName) {
        fileName = fileName.concat(".realm");
        try {
            File dir = new File(DIRECTORY);
            //noinspection ResultOfMethodCallIgnored
            dir.mkdir();
            final File file = new File(DIRECTORY, fileName);
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            realm.writeCopyTo(file);
            return file;
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_invalid_file_name), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void exportBackUpFile() {
        String fileName = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()).concat("_backup");
        File file = makeBackUpFile(fileName);
        if (file == null) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            return;
        }
        Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"xema027@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.message_choose_export_application)));
    }

    private void importBackUpFile(Uri uri) {
        String path = copyBundledRealmFile(uri);
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        } else {
            //어플리케이션 재시작
            Toast.makeText(this, getString(R.string.message_import_back_up_complete), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private String copyBundledRealmFile(Uri uri) {
        try {
            File file = new File(realm.getPath());
            realm.close();
            FileOutputStream outputStream = new FileOutputStream(file);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            //do nothing
        } catch (java.io.IOException e) {
            //do nothing
        }
        return null;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "업로드할 파일을 선택해주세요"), Constants.REQUEST_CODE_SELECT_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "파일 매니저 어플리케이션이 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SELECT_FILE && resultCode == RESULT_OK) {
            importBackUpFile(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSION_MAKE_BACK_UP_FILE:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    showFileNameDialog();
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
            case PermissionUtil.PERMISSION_EXPORT_BACK_UP_FILE:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    exportBackUpFile();
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
            case PermissionUtil.PERMISSION_IMPORT_BACK_UP_FILE:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    showFileChooser();
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
        }
    }
}
