package com.xema.shopmanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.CustomerAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.enums.SortType;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.User;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;
import com.xema.shopmanager.ui.dialog.SortBottomSheetDialog;
import com.xema.shopmanager.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = CustomerActivity.class.getSimpleName();
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.nv_drawer)
    NavigationView nvDrawer;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;

    private Realm realm;

    private CustomerAdapter mAdapter;

    private RealmAsyncTask transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);

        initToolbar();
        initDrawer();
        initListeners();
        setUpAdapter();
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
        tbMain.setTitle(getString(R.string.app_name));
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();
        nvDrawer.setNavigationItemSelectedListener(this);
        View headerView = nvDrawer.getHeaderView(0);
        //ImageView bgView = headerView.findViewById(R.id.iv_header_bg);
        //GlideApp.with(this).load(R.drawable.bg_header).override(20, 100).apply(RequestOptions.bitmapTransform(new BlurTransformation(16))).into(bgView);
        ImageView settingView = headerView.findViewById(R.id.iv_setting);
        settingView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileSettingActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_PROFILE_SETTING);
        });
        updateDrawer();
    }

    private void updateDrawer() {
        if (realm == null) return;
        User user = PreferenceHelper.loadUser(this);
        if (user == null) return;
        View headerView = nvDrawer.getHeaderView(0);
        RoundedImageView roundedImageView = headerView.findViewById(R.id.riv_profile);
        TextView nameView = headerView.findViewById(R.id.tv_name);
        nameView.setText(user.getName());
        TextView businessNameView = headerView.findViewById(R.id.tv_business_name);
        GlideApp.with(this).load(user.getProfileImage()).error(R.drawable.ic_profile_default).into(roundedImageView);
        businessNameView.setText(TextUtils.isEmpty(user.getBusinessName()) ? getString(R.string.message_no_input_business_name) : user.getBusinessName());
    }

    private void initListeners() {
        // TODO: 2019-01-10
        srlMain.setOnRefreshListener(() -> {
            mAdapter.refresh();
            updateUI();
        });
    }

    private void setUpAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMain.setLayoutManager(layoutManager);
        mAdapter = new CustomerAdapter(this, realm, queryData());
        mAdapter.setHasStableIds(true);
        mAdapter.setOnDeleteListener(this::showDeleteDialog);
        mAdapter.setOnEditListener(this::attemptEdit);
        rvMain.setAdapter(mAdapter);

        updateUI();
    }

    //Managed list 는 custom sort 불가..
    private RealmResults<Person> queryData() {
        if (realm == null) return null;
        RealmQuery<Person> q = realm.where(Person.class);
        switch (PreferenceHelper.loadSortMode(this)) {
            case NAME:
                q.sort("name", Sort.ASCENDING);
                break;
            case CREATE:
                q.sort("createdAt", Sort.DESCENDING);
                break;
            default:
                break;
        }
        return q.findAll();
    }

    private void updateUI() {
        if (mAdapter == null) return;

        int size = mAdapter.getData() == null ? 0 : mAdapter.getData().size();

        if (size <= 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);
        }
        if (srlMain != null && srlMain.isRefreshing())
            srlMain.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (dlMain.isDrawerOpen(GravityCompat.START)) {
            dlMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //REALM ADAPTER 에서 AUTO UPDATE 사용중
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_CUSTOMER && resultCode == RESULT_OK) {
            updateUI();
            //edtSearch.getText().clear();
            //CommonUtil.hideKeyboard(this);
            //queryList();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_SALES && resultCode == RESULT_OK) {
            //edtSearch.getText().clear();
            //CommonUtil.hideKeyboard(this);
            //queryList();
        } else if (requestCode == Constants.REQUEST_CODE_EDIT_CUSTOMER && resultCode == RESULT_OK) {
            //edtSearch.getText().clear();
            //CommonUtil.hideKeyboard(this);
            //queryList();
        } else if (requestCode == Constants.REQUEST_CODE_CATEGORY && resultCode == RESULT_OK) {
            //edtSearch.getText().clear();
            //CommonUtil.hideKeyboard(this);
            //queryList();
        } else if (requestCode == Constants.REQUEST_CODE_EDIT_PROFILE_SETTING && resultCode == RESULT_OK) {
            updateDrawer();
        }
    }

    private void showDeleteDialog(Person person, int position) {
        SimpleTextDialog dialog = new SimpleTextDialog(this, getString(R.string.alert_delete_customer, person.getName()));
        dialog.setOnPositiveListener(this.getString(R.string.common_delete), () -> {
            deletePerson(person, position);
        });
        dialog.show();
    }

    // TODO: 2018-07-03 RealmUtil 로 따로 빼기
    private void deletePerson(Person person, int position) {
        final String id = person.getId();

        transaction = realm.executeTransactionAsync(realm -> {
            Person deletePerson = realm.where(Person.class).equalTo("id", id).findFirst();
            if (deletePerson == null) return;

            RealmList<Sales> sales = deletePerson.getSales();
            for (Sales sale : sales) {
                sale.getPurchases().deleteAllFromRealm();
            }
            sales.deleteAllFromRealm();
            deletePerson.deleteFromRealm();
        }, () -> {
            mAdapter.refresh();
            updateUI();
            Toast.makeText(CustomerActivity.this, getString(R.string.message_delete_success), Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(CustomerActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show());
    }

    private void attemptEdit(Person person, int position) {
        Intent intent = new Intent(this, EditCustomerActivity.class);
        intent.putExtra("personId", person.getId());
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sort) {
            SortBottomSheetDialog dialog = new SortBottomSheetDialog(this, R.style.FullScreenTransparentDialog);
            dialog.setOnSortListener(this::attemptSort);
            dialog.show();
            return true;
        } else
        if (id == R.id.menu_add_customer) {
            Intent intent = new Intent(CustomerActivity.this, AddCustomerActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_CUSTOMER);
        } else if (id == R.id.menu_search) {
            Intent intent = new Intent(CustomerActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSort(SortType sort) {
        PreferenceHelper.saveSortMode(CustomerActivity.this, sort);
       mAdapter.refresh();
        updateUI();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_nav_category) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_CATEGORY);
        } else if (id == R.id.menu_nav_data) {
            Intent intent = new Intent(this, BackUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_nav_analysis) {
            Intent intent = new Intent(this, ChartListActivity.class);
            startActivity(intent);
        }
        //else if (id == R.id.menu_nav_setting) {
        //    Intent intent = new Intent(this, SettingActivity.class);
        //    startActivityForResult(intent, Constants.REQUEST_CODE_SETTING);
        //}
        else if (id == R.id.menu_nav_review) {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.menu_nav_report) {
            Uri uri = Uri.parse(CommonUtil.makeReportString(this));
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(it);
        } else if (id == R.id.menu_nav_version) {
            Intent intent = new Intent(this, VersionActivity.class);
            startActivity(intent);
        }

        dlMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
