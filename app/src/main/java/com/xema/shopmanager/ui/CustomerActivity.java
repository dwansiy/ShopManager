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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.PersonAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.wrapper.ProductWrapper;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

// TODO: 2018-02-26 sales 추가하고나서 refresh
// TODO: 2018-02-26 정렬은 쿼리문 단계에서 구분하고 preference에 저장. 한번 선택한 정렬형식이 계속 가도록
public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.nv_drawer)
    NavigationView nvDrawer;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    private Realm realm;

    private List<Person> mList;
    private PersonAdapter mAdapter;

    private enum Sort {
        NAME, RECENT, PRICE, CREATE, VISIT
    }

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
        queryCustomers();
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
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbMain.setTitle(getString(R.string.common_loading));
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();

        nvDrawer.setNavigationItemSelectedListener(this);
    }

    private void initListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, AddActivity.class);
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
            //전체 표시
            queryCustomers();
        } else {
            queryCustomers(s);
        }
    }

    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new PersonAdapter(this, realm, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
    }

    private void queryCustomers() {
        RealmResults<Person> results = realm.where(Person.class).sort("name").findAll();
        if (results == null || results.size() == 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);

            if (mList != null) mList.clear();
            else mList = new ArrayList<>();
            mList.addAll(results);
            mAdapter.notifyDataSetChanged();
        }

        // TODO: 2018-02-16 ui업데이트할것 메소드 따로 분리해서
        tbMain.setTitle(getString(R.string.format_count_customer, mList.size()));
    }

    private void queryCustomers(String s) {
        RealmResults<Person> results = realm.where(Person.class).sort("name").contains("name", s).or().contains("phone", s).findAll();
        if (mList != null) mList.clear();
        else mList = new ArrayList<>();
        mList.addAll(results);
        mAdapter.notifyDataSetChanged();

        // TODO: 2018-02-16 ui업데이트할것 메소드 따로 분리해서
        tbMain.setTitle(getString(R.string.format_search_customer, mList.size()));
    }

    // TODO: 2018-02-26 현재 검색결과로 할까 전체로할까...흠
    private void queryCustomers(Sort sort) {
        if (mList == null || mList.size() == 0) return;

        switch (sort) {
            case NAME:
                Collections.sort(mList, new NameComparator());
                break;
            case RECENT:
                Collections.sort(mList, new RecentComparator());
                break;
            case PRICE:
                Collections.sort(mList, new PriceComparator());
                break;
            case VISIT:
                Collections.sort(mList, new VisitComparator());
                break;
            case CREATE:
                Collections.sort(mList, new CreateComparator());
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    //static <T extends Comparable<T>> int cp(T a, T b) {
    //    return a == null ? (b == null ? 0 : Integer.MIN_VALUE) : (b == null ? Integer.MAX_VALUE : a.compareTo(b));
    //}

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
                    RealmList<ProductWrapper> productWrappers = sales.getProductWrappers();
                    if (productWrappers != null && productWrappers.size() != 0) {
                        for (ProductWrapper wrapper : productWrappers) {
                            price1 += wrapper.getCount() * wrapper.getProduct().getPrice();
                        }
                    }
                }
            }

            long price2 = 0;
            RealmList<Sales> sales2 = o2.getSales();
            if (sales2 != null && sales2.size() != 0) {
                for (Sales sales : sales2) {
                    RealmList<ProductWrapper> productWrappers = sales.getProductWrappers();
                    if (productWrappers != null && productWrappers.size() != 0) {
                        for (ProductWrapper wrapper : productWrappers) {
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
            queryCustomers();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_SALES && resultCode == RESULT_OK) {
            queryCustomers();
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

        if (id == R.id.menu_sort_name) {
            queryCustomers(Sort.NAME);
            return true;
        } else if (id == R.id.menu_sort_recent) {
            queryCustomers(Sort.RECENT);
            return true;
        } else if (id == R.id.menu_sort_price) {
            queryCustomers(Sort.PRICE);
            return true;
        } else if (id == R.id.menu_sort_visit) {
            queryCustomers(Sort.VISIT);
            return true;
        } else if (id == R.id.menu_sort_create) {
            queryCustomers(Sort.CREATE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: 2018-02-10
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
