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
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.ui.dialog.MakeBackUpFileDialog;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;
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
            showImportAlertDialog();
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

    // TODO: 2018-08-29 버전에 따라 스키마가 다른 경우는 어떻게 해결해야하지? -> v1:원본 v2:Person 에 필드 추가됨. 인 상황.
    // TODO: 2018-08-29 case 1 : 기기의 DB 버전 v2. import v1 -> SyncConfig 사용하지 않으면 SchemaMigration 수행하므로 문제 x. import v2->문제 x
    // TODO: 2018-08-29 case 2 : 기기의 DB 버전 v1(예전꺼 쓰고있을경우) import v1->문제 x. import v2 : person field 충돌.... 업데이트를 강제해야하나?
    // TODO: 2018-08-29 업데이트를 강제하기 위해서는 네트워크 확인도 필요. alert 띄우고 업데이트 권유가 나을듯. 충돌날수도 있다는 권유로.
    // TODO: 2018-08-29 제일 좋은 방법은 기기의 db 버전과 import 하는 realm 파일의 버전을 확인하는것
    // TODO: 2018-08-29 기기 db버전은 확인 가능해도 import 하는 파일의 버전을 확인 어떻게하지... realm에 메일보내서 물어볼수도?
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

    private void showImportAlertDialog() {
        String s = getString(R.string.message_dialog_import_alert_html) + "\n\n" + "<a href=\"https://play.google.com/store/apps/details?id=com.xema.shopmanager\">마켓으로 가기</a>";
        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            // TODO: 2018-08-26  에러 확인해보기
            spanned = Html.fromHtml(s);
        }
        SimpleTextDialog dialog = new SimpleTextDialog(this, spanned);
        dialog.setOnPositiveListener(getString(R.string.action_import), this::showFileChooser);
        dialog.show();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.message_chhose_upload_file)), Constants.REQUEST_CODE_SELECT_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.error_not_have_file_explorer), Toast.LENGTH_SHORT).show();
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
                    showImportAlertDialog();
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
        }
    }
}
