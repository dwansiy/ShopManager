package com.xema.shopmanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.xema.shopmanager.model.Person;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.Sort;

/**
 * Created by xema0 on 2018-02-15.
 */

public class AddActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.edt_memo)
    EditText edtMemo;

    private Realm realm;
    private RealmAsyncTask transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
    }

    @Override
    protected void onStop() {
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }
        super.onStop();
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
        ivDone.setOnClickListener(this::attemptRegister);

        edtMemo.addTextChangedListener(new TextWatcher() {
            private final int MAX_LENGTH = 50;

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
    }

    private void attemptRegister(View view) {
        final String name = edtName.getText().toString();
        final String phone = edtPhone.getText().toString(); //하이픈 제거해야하나? 딱히 할필요는 없을듯...
        final String memo = edtMemo.getText().toString();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone)) {
            Snackbar.make(edtMemo, getString(R.string.error_not_exist_name_phone), Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (realm == null) return;

        view.setEnabled(false);

        final Person person = new Person();
        person.setName(name);
        person.setPhone(phone);
        person.setMemo(memo);

        transaction = realm.executeTransactionAsync(bgRealm -> {
            bgRealm.copyToRealm(person);
        }, () -> {
            Toast.makeText(AddActivity.this, getString(R.string.message_add_complete), Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
            // TODO: 2018-02-17 intent data 세팅
            setResult(RESULT_OK);
            finish();
        }, error -> {
            //error.printStackTrace();
            Toast.makeText(AddActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
        });
    }
}
