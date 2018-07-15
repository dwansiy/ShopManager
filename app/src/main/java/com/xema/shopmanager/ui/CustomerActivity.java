package com.xema.shopmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.CustomerAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.comparator.PersonCreateComparator;
import com.xema.shopmanager.comparator.PersonNameComparator;
import com.xema.shopmanager.comparator.PersonPriceComparator;
import com.xema.shopmanager.comparator.PersonRecentComparator;
import com.xema.shopmanager.comparator.PersonVisitComparator;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Profile;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;
import com.xema.shopmanager.ui.dialog.SortBottomSheetDialog;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.utils.DelayTextWatcher;
import com.xema.shopmanager.utils.InitialSoundUtil;
import com.xema.shopmanager.widget.QuickPanelTipView;
import com.xema.shopmanager.widget.QuickPanelView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;

// TODO: 2018-07-03 quick panel 나타나는거 조정할수있게 옵션화면 만들기 -> 사용자 이름, 가게명 바꾸기나 등등도 가능하도록
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
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;
    @BindView(R.id.qpv_main)
    QuickPanelView qpvMain;
    @BindView(R.id.qptv_main)
    QuickPanelTipView qptvMain;

    private Realm realm;

    private List<Person> mList;
    private CustomerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private Vibrator mVibrator;

    private RealmAsyncTask transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initToolbar();
        initDrawer();
        updateList();
        initAdapter();
        initListeners();

        updateUI();
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
        edtSearch.addTextChangedListener(new DelayTextWatcher() {
            @Override
            public void delayedChanged(Editable editable) {
                runOnUiThread(() -> attemptSearch(editable.toString()));
            }
        });
        srlMain.setOnRefreshListener(() -> {
            attemptSearch(edtSearch.getText().toString());
        });
        qpvMain.setOnQuickSideBarTouchListener(mQuickPanelListener);
        rvMain.addOnScrollListener(mQuickPanelVisibilityListener);
        mAdapter.setOnDeleteListener(this::showDeleteDialog);
        mAdapter.setOnEditListener(this::attemptEdit);
    }

    private RecyclerView.OnScrollListener mQuickPanelVisibilityListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                qpvMain.setVisibility(View.VISIBLE);
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                delayedHide();
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private Handler mHideHandler = new Handler();
    private Runnable mHideRunnable = () -> qpvMain.setVisibility(View.GONE);

    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 1800);
    }

    private QuickPanelView.OnQuickSideBarTouchListener mQuickPanelListener = new QuickPanelView.OnQuickSideBarTouchListener() {
        @Override
        public void onLetterChanged(String letter, int position, float y) {
            qptvMain.setText(letter, position, y);
            mVibrator.vibrate(2);
            if (mList == null || mList.isEmpty()) return;

            for (int i = 0; i < mList.size(); i++) {
                Person person = mList.get(i);
                String name = person.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (InitialSoundUtil.matchString(name.substring(0, 1), letter)) {
                        mLayoutManager.scrollToPositionWithOffset(i, 0);
                        //rvMain.scrollToPosition(mList.indexOf(person));
                        return;
                    }
                }
            }
        }

        @Override
        public void onLetterTouching(boolean touching) {
            if (!touching) {
                delayedHide();
                qptvMain.setVisibility(View.GONE);
            } else {
                mHideHandler.removeCallbacks(mHideRunnable);
                qptvMain.setVisibility(View.VISIBLE);
            }
        }
    };

    private void attemptSearch(String s) {
        if (TextUtils.isEmpty(s)) {
            updateList();
            updateUI();
        } else {
            updateFilteredList(s);
            updateUI();
            tbMain.setTitle(getString(R.string.format_search_customer, mList.size()));
        }
    }

    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mAdapter = new CustomerAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
    }

    private void updateList() {
        if (realm == null) return;
        if (mList != null)
            mList.clear();
        else mList = new ArrayList<>();
        mList.addAll(realm.copyFromRealm(realm.where(Person.class).findAll()));
    }

    private void updateFilteredList(String searchText) {
        if (realm == null) return;
        if (mList != null)
            mList.clear();
        else mList = new ArrayList<>();
        List<Person> filteredList = realm.copyFromRealm(realm.where(Person.class).findAll());
        for (Person person : filteredList) {
            String name = person.getName();
            String phone = person.getPhone();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                if (name.contains(searchText)) {
                    mList.add(person);
                } else if (InitialSoundUtil.matchString(name, searchText)) {
                    mList.add(person);
                } else if (phone.contains(searchText)) {
                    mList.add(person);
                }
            } else if (!TextUtils.isEmpty(name)) {
                if (name.contains(searchText)) {
                    mList.add(person);
                } else if (InitialSoundUtil.matchString(name, searchText)) {
                    mList.add(person);
                }
            } else if (!TextUtils.isEmpty(phone)) {
                if (phone.contains(searchText)) {
                    mList.add(person);
                }
            }
        }
    }

    private void updateUI() {
        if (mList == null || mList.isEmpty()) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            sort(PreferenceHelper.loadSortMode(this));
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);
        }
        tbMain.setTitle(getString(R.string.format_count_customer, mList.size()));

        mAdapter.notifyDataSetChanged();

        if (srlMain.isRefreshing()) srlMain.setRefreshing(false);
    }

    //private void queryCustomers() {
    //    mList = realm.where(Person.class).findAll();
    //    updateUI();
    //}

    //private void queryCustomers(String s) {
    //    mList = realm.where(Person.class).contains("name", s).or().contains("phone", s).findAll();
    //    updateUI();
    //}

    //private List<Person> getFilteredList(String s){
    //    return realm.copyFromRealm(realm.where(Person.class).contains("name", s).or().contains("phone", s).findAll(););
    //}

    private void sort(Constants.Sort sort) {
        if (mList == null || mList.size() == 0) return;
        switch (sort) {
            case NAME:
                Collections.sort(mList, new PersonNameComparator());
                break;
            case PRICE:
                Collections.sort(mList, new PersonPriceComparator());
                break;
            case VISIT:
                Collections.sort(mList, new PersonVisitComparator());
                break;
            case CREATE:
                Collections.sort(mList, new PersonCreateComparator());
                break;
            case RECENT:
                Collections.sort(mList, new PersonRecentComparator());
                break;
            default:
                break;
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
            updateList();
            updateUI();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_SALES && resultCode == RESULT_OK) {
            edtSearch.getText().clear();
            CommonUtil.hideKeyboard(this);
            updateList();
            updateUI();
        } else if (requestCode == Constants.REQUEST_CODE_EDIT_CUSTOMER && resultCode == RESULT_OK) {
            attemptSearch(edtSearch.getText().toString());
            //edtSearch.getText().clear();
            //CommonUtil.hideKeyboard(this);
            //updateList();
            //updateUI();
        } else if (requestCode == Constants.REQUEST_CODE_CATEGORY && resultCode == RESULT_OK) {
            edtSearch.getText().clear();
            CommonUtil.hideKeyboard(this);
            updateList();
            updateUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        //updateList();
        //updateUI();

        attemptSearch(edtSearch.getText().toString());
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
            attemptSearch(edtSearch.getText().toString());
            Toast.makeText(CustomerActivity.this, getString(R.string.message_delete_success), Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(CustomerActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show());
    }

    private void attemptEdit(Person person, int position) {
        Intent intent = new Intent(this, EditCustomerActivity.class);
        intent.putExtra("personId", person.getId());
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_nav_category) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_CATEGORY);
        } else if (id == R.id.menu_nav_analysis) {
            Intent intent = new Intent(this, ChartListActivity.class);
            startActivity(intent);
        }

        dlMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
