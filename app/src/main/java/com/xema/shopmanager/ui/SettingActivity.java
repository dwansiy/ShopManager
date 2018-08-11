package com.xema.shopmanager.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.utils.PermissionUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by xema0 on 2018-07-24.
 */

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    //@BindView(R.id.ll_setting_quick_panel)
    //LinearLayout llSettingQuickPanel;
    @BindView(R.id.ll_setting_contact)
    LinearLayout llSettingContact;
    //@BindView(R.id.s_quick_panel)
    //Switch sQuickPanel;
    @BindView(R.id.pb_contact)
    ProgressBar pbContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initToolbar();
        //initPreferenceData();
        initListeners();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    //Deprecated
    //private void initPreferenceData() {
    //    sQuickPanel.setChecked(PreferenceHelper.loadQuickPanel(this));
    //}

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        //llSettingQuickPanel.setOnClickListener(this::attemptQuickPanel);
        llSettingContact.setOnClickListener(this::attemptAddDeviceContacts);
    }

    //private void attemptQuickPanel(View view) {
    //    boolean checked = sQuickPanel.isChecked();
    //    sQuickPanel.setChecked(!checked);
    //    PreferenceHelper.saveQuickPanel(this, !checked);
    //}

    private void attemptAddDeviceContacts(View view) {
        if (PermissionUtil.checkAndRequestPermission(this, PermissionUtil.PERMISSION_CONTACT, Manifest.permission.READ_CONTACTS)) {
            new ContactAsyncTask(this).execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSION_CONTACT:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    new ContactAsyncTask(this).execute();
                } else {
                    PermissionUtil.showRationalDialog(this, getString(R.string.permission_need_permission));
                }
                break;
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    // TODO: 2018-07-26  이름으로 중복체크하면 안되고... 이름+폰번호?? 애매하네...
    private static final class ContactAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<SettingActivity> ref;
        private static final String[] PROJECTION = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

        ContactAsyncTask(SettingActivity ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SettingActivity activity = ref.get();
            if (activity == null) return;

            activity.llSettingContact.setEnabled(false);
            activity.pbContact.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SettingActivity activity = ref.get();
            if (activity == null) return;

            activity.llSettingContact.setEnabled(true);
            activity.pbContact.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Activity activity = ref.get();
            if (activity == null) return null;

            ContentResolver cr = activity.getContentResolver();
            if (cr == null) return null;
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");

            if (cur == null) return null;

            try (Realm bgRealm = Realm.getDefaultInstance()) {
                List<Person> contacts = new ArrayList<>();
                //List<String> names = new ArrayList<>();
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phone = "";
                    Cursor phoneCursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (phoneCursor == null) continue;
                    if (phoneCursor.moveToFirst()) {
                        phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!TextUtils.isEmpty(phone)) phone = phone.replaceAll("-", "");
                    }
                    phoneCursor.close();
                    Person p = bgRealm.where(Person.class).equalTo("name", name).findFirst();
                    if (p == null) {
                        Person person = new Person();
                        person.setName(name);
                        if (!TextUtils.isEmpty(phone))
                            person.setPhone(phone);
                        contacts.add(person);
                    }
                    //names.add(name);
                }
                bgRealm.beginTransaction();
                bgRealm.copyToRealmOrUpdate(contacts);
                bgRealm.commitTransaction();

                bgRealm.close();
            }
            cur.close();
            return null;
        }
    }
}
