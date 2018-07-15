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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;

/**
 * Created by xema0 on 2018-02-15.
 */

public class EditCustomerActivity extends AppCompatActivity {
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

    private String personId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initListeners();

        if (getIntent() != null) {
            personId = getIntent().getStringExtra("personId");
            if (!TextUtils.isEmpty(personId)) {
                Person person = queryPerson(personId);
                if (person == null) {
                    Toast.makeText(this, getString(R.string.error_deleted_customer), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                updateUI(person);
                CommonUtil.focusLastCharacter(edtName);
            }
        }
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
        ivDone.setOnClickListener(this::attemptEdit);
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
        edtName.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:
                    edtPhone.requestFocus();
                    CommonUtil.focusLastCharacter(edtPhone);
                    break;
                default:
                    return false;
            }
            return true;
        });
        edtPhone.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_NEXT:
                    edtMemo.requestFocus();
                    CommonUtil.focusLastCharacter(edtMemo);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private Person queryPerson(String personId) {
        final Person person = realm.where(Person.class).equalTo("id", personId).findFirst();
        if (person == null) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            return null;
        }
        return person;
    }

    private void updateUI(Person person) {
        if (person == null) return;

        edtName.setText(person.getName());
        edtPhone.setText(person.getPhone());
        edtMemo.setText(person.getMemo());
    }

    private void attemptEdit(View view) {
        final String name = edtName.getText().toString();
        final String phone = edtPhone.getText().toString();
        final String memo = edtMemo.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Snackbar.make(edtMemo, getString(R.string.error_not_exist_name), Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (realm == null) return;

        view.setEnabled(false);

        transaction = realm.executeTransactionAsync(bgRealm -> {
            final Person person = bgRealm.where(Person.class).equalTo("id", personId).findFirst();
            if (person == null) {
                runOnUiThread(() -> Toast.makeText(EditCustomerActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show());
                view.setEnabled(true);
                return;
            }
            person.setName(name);
            person.setPhone(phone);
            person.setMemo(memo);
            bgRealm.copyToRealmOrUpdate(person);
        }, () -> {
            Toast.makeText(EditCustomerActivity.this, getString(R.string.message_edit_complete), Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
            setResult(RESULT_OK);
            finish();
        }, error -> {
            //error.printStackTrace();
            Toast.makeText(EditCustomerActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
        });
    }
}
