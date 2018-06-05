package com.xema.shopmanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.SalesAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.wrapper.ProductWrapper;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by xema0 on 2018-02-21.
 */

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_visit)
    TextView tvVisit;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_recent)
    TextView tvRecent;
    @BindView(R.id.tv_memo)
    TextView tvMemo;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.iv_edit)
    ImageView ivEdit;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.fab_call)
    FloatingActionButton fabCall;

    private String id;
    private Realm realm;

    private RealmList<Sales> mList;
    private SalesAdapter mAdapter;

    private Person person;

    private boolean needUpdate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null)
            id = intent.getStringExtra("id");
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            finish();
        }

        initToolbar();

        person = query(id);
        if (person == null) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initListeners();
        initAdapter();

        updateUI(person, mList);
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
        ivEdit.setOnClickListener(this::attemptEdit);
        fabAdd.setOnClickListener(this::attemptAdd);
        fabCall.setOnClickListener(this::attemptCall);
    }

    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        //mList = new RealmList<>();
        mList = person.getSales();
        mAdapter = new SalesAdapter(this, realm, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
    }

    private Person query(String id) {
        return realm.where(Person.class).equalTo("id", id).findFirst();
    }

    private void updateUI(Person person, RealmList<Sales> sales) {
        mAdapter.notifyDataSetChanged();

        tvName.setText(person.getName());
        tvPhone.setText(CommonUtil.toHypenFormat(person.getPhone()));
        final String memo = person.getMemo();
        if (TextUtils.isEmpty(memo)) {
            tvMemo.setText("X");
        } else {
            tvMemo.setText(memo);
        }

        if (sales == null || sales.size() == 0) {
            tvRecent.setText("X");
            tvVisit.setText(String.valueOf(0));
            tvTotal.setText(getString(R.string.format_price, String.valueOf(0)));
        } else {
            Date recentAt = sales.maxDate("selectedAt");
            tvRecent.setText(CommonUtil.getModifiedDate(recentAt));
            tvVisit.setText(String.valueOf(sales.size()));

            long total = 0;
            for (Sales item : sales) {
                RealmList<ProductWrapper> productWrappers = item.getProductWrappers();
                for (ProductWrapper wrapper : productWrappers) {
                    total += wrapper.getCount() * wrapper.getProduct().getPrice();
                }
            }
            tvTotal.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(total)));
        }

        //tvVisit.setText(String.valueOf(person.getVisit()));
        //Date recentAt = person.getRecentAt();
        //if (recentAt == null) {
        //    tvRecent.setText("X");
        //} else {
        //    tvRecent.setText(CommonUtil.getModifiedDate(person.getRecentAt()));
        //}
        //tvTotal.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(person.getTotalPrice())));

        shouldShowEmptyView();
    }

    private void shouldShowEmptyView() {
        if (mList == null || mList.size() == 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);
        }
    }

    private boolean shouldShowCategorySnackBar() {
        RealmResults<Category> results = realm.where(Category.class).findAll();
        return results == null || results.size() == 0;
    }

    private boolean shouldShowProductSnackBar() {
        RealmResults<Product> results = realm.where(Product.class).findAll();
        return results == null || results.size() == 0;
    }

    private void attemptEdit(View v) {
        Intent intent = new Intent(this, EditCustomerActivity.class);
        intent.putExtra("personId", person.getId());
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_CUSTOMER);
    }


    private void attemptAdd(View v) {
        if (shouldShowCategorySnackBar()) {
            Snackbar.make(fabAdd, getString(R.string.error_no_category), Snackbar.LENGTH_LONG).setAction(getString(R.string.common_register), view -> {
                Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
                startActivity(intent);
            }).show();
        } else if (shouldShowProductSnackBar()) {
            Snackbar.make(fabAdd, getString(R.string.error_no_product), Snackbar.LENGTH_LONG).setAction(getString(R.string.common_register), view -> {
                Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
                startActivity(intent);
            }).show();
        } else {
            Intent intent = new Intent(this, SalesActivity.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SALES);
        }
    }

    private void attemptCall(View view) {
        String phone = person.getPhone();
        if (TextUtils.isEmpty(phone)) {
            Snackbar.make(view, getString(R.string.error_not_registered_phone), Snackbar.LENGTH_LONG).setAction(getString(R.string.common_register), v -> {
                attemptEdit(view);
            }).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_ADD_SALES && resultCode == RESULT_OK) {
            updateUI(person, mList);
            needUpdate = true;
        }
        if (requestCode == Constants.REQUEST_CODE_EDIT_CUSTOMER && resultCode == RESULT_OK) {
            person = query(id);
            updateUI(person, mList);
            needUpdate = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        if (needUpdate) setResult(RESULT_OK);
        super.finish();
    }


}
