package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.PersonAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Profile;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.dialog.SortBottomSheetDialog;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.nv_drawer)
    NavigationView nvDrawer;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;

    private Realm realm;

    private RealmResults<Person> mList;
    private PersonAdapter mAdapter;

    private RealmChangeListener<RealmResults<Person>> mChangeListener = realmResults -> updateUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initDrawer();
        initListeners();
        initAdapter();

        updateUI();
    }

    @Override
    protected void onStop() {
        if (mList != null)
            mList.removeAllChangeListeners();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rvMain.setAdapter(null);
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        tbMain.setTitle(getString(R.string.common_loading));
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();

        nvDrawer.setNavigationItemSelectedListener(this);

        View headerView = nvDrawer.getHeaderView(0);
        RoundedImageView roundedImageView = headerView.findViewById(R.id.riv_profile);
        TextView nameView = headerView.findViewById(R.id.tv_name);
        TextView businessNameView = headerView.findViewById(R.id.tv_business_name);

        Profile profile = realm.where(Profile.class).findFirst();

        if (profile == null) return;

        GlideApp.with(this).load(profile.getProfileImage()).error(R.drawable.ic_profile_default).into(roundedImageView);
        nameView.setText(profile.getName());
        businessNameView.setText(profile.getBusinessName());
    }

    private void initListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, AddCustomerActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_CUSTOMER);
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                attemptSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void attemptSearch(String s) {
        if (TextUtils.isEmpty(s)) {
            queryCustomers();
        } else {
            queryCustomers(s);
        }
    }

    private void initAdapter() {
        mList = realm.where(Person.class).findAll();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mAdapter = new PersonAdapter(this, mList, true, realm);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);

        mList.addChangeListener(mChangeListener);
    }

    private void updateUI() {
        if (mList.size() == 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);
        }

        tbMain.setTitle(getString(R.string.format_count_customer, mList.size()));

        mAdapter.setDataList(mList);
        mAdapter.updateData(mList);
    }

    private void queryCustomers() {
        mList = realm.where(Person.class).findAll();
        updateUI();
    }

    private void queryCustomers(String s) {
        mList = realm.where(Person.class).contains("name", s).or().contains("phone", s).findAll();
        updateUI();
    }

    // TODO: 2018-06-08  소트
    private void queryCustomers(Constants.Sort sort) {
        if (mList == null || mList.size() == 0) return;

        switch (sort) {
            case NAME:
                mList = mList.sort("name", Sort.ASCENDING);
                updateUI();
                //Collections.sort(mList, new NameComparator());
                break;
            case RECENT:
                //List<Person> storesList = realm.copyFromRealm(mList);
                //Collections.sort(storesList, new RecentComparator());
                //mList = (RealmResults<Person>) storesList;
                //updateUI();
                break;
            case PRICE: {
                //List<Person> storesList = realm.copyFromRealm(mList);
                //Collections.sort(storesList, new PriceComparator());
                //mList = (RealmResults<Person>) storesList;
                //updateUI();
            }
            break;
            case VISIT: {
                //List<Person> storesList = realm.copyFromRealm(mList);
                //Collections.sort(storesList, new VisitComparator());
                //mList = (RealmResults<Person>) storesList;
                //updateUI();
            }
            break;
            case CREATE:
                mList = mList.sort("createdAt", Sort.ASCENDING);
                updateUI();
                //Collections.sort(mList, new CreateComparator());
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }


    private class NameComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private class RecentComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            Date date1 = o1.getSales().maxDate("selectedAt");
            Date date2 = o2.getSales().maxDate("selectedAt");

            return date2 == null ? (date1 == null ? 0 : Integer.MIN_VALUE) : (date1 == null ? Integer.MAX_VALUE : date2.compareTo(date1));
        }
    }

    // TODO: 2018-02-26 리팩토링 시급
    private class PriceComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            long price1 = 0;
            RealmList<Sales> sales1 = o1.getSales();
            if (sales1 != null && sales1.size() != 0) {
                for (Sales sales : sales1) {
                    RealmList<Purchase> purchases = sales.getPurchases();
                    if (purchases != null && purchases.size() != 0) {
                        for (Purchase wrapper : purchases) {
                            price1 += wrapper.getCount() * wrapper.getProduct().getPrice();
                        }
                    }
                }
            }

            long price2 = 0;
            RealmList<Sales> sales2 = o2.getSales();
            if (sales2 != null && sales2.size() != 0) {
                for (Sales sales : sales2) {
                    RealmList<Purchase> purchases = sales.getPurchases();
                    if (purchases != null && purchases.size() != 0) {
                        for (Purchase wrapper : purchases) {
                            price2 += wrapper.getCount() * wrapper.getProduct().getPrice();
                        }
                    }
                }
            }

            return Long.compare(price2, price1);
        }
    }

    private class VisitComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return Integer.compare(o2.getSales().size(), o1.getSales().size());
        }
    }

    //내림차순(최근이 제일 위로로)
    private class CreateComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
        }
    }

    @Override
    public void onBackPressed() {
        if (dlMain.isDrawerOpen(GravityCompat.START)) {
            dlMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_CUSTOMER && resultCode == RESULT_OK) {
            edtSearch.getText().clear();
            CommonUtil.hideKeyboard(this);
            updateUI();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_SALES && resultCode == RESULT_OK) {
            updateUI();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sort, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSort(Constants.Sort sort) {
        PreferenceHelper.saveSortMode(CustomerActivity.this, sort);
        queryCustomers(sort);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_nav_category) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_nav_analysis) {
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
        }

        dlMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
