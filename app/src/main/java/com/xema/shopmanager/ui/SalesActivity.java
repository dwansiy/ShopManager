package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.SalesRegisterAdapter;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.wrapper.CategoryWrapper;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by xema0 on 2018-02-24.
 */

// TODO: 2018-02-25 전체 리팩토링
public class SalesActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.cv_calendar)
    CalendarView cvCalendar;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.edt_memo)
    EditText edtMemo;
    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;

    private List<CategoryWrapper> mCategoryList;
    private SalesRegisterAdapter mAdapter;

    private Realm realm;
    private RealmAsyncTask transaction;

    private Sales mData;

    private String personId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
            return;
        }

        personId = getIntent().getStringExtra("id");

        initToolbar();
        initListeners();
        initAdapter();

        queryCategory();

        // TODO: 2018-02-25 수정일경우는 쿼리해서 대입
        //initSalesData();

        nsvMain.scrollBy(0, 0);
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
        cvCalendar.setMaxDate(System.currentTimeMillis());
        cvCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            view.setDate(c.getTimeInMillis());
        });
    }

    private void initAdapter() {
        mCategoryList = new ArrayList<>();
        mAdapter = new SalesRegisterAdapter(this, mCategoryList);
        rvMain.setAdapter(mAdapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.setHasFixedSize(false);

        rvMain.setNestedScrollingEnabled(false);
    }

    private void queryCategory() {
        RealmResults<Category> results = realm.where(Category.class).findAll();
        if (mCategoryList != null) mCategoryList.clear();
        else mCategoryList = new ArrayList<>();

        for (Category category : results) {
            CategoryWrapper wrapper = new CategoryWrapper();
            wrapper.setCategory(category);
            RealmList<Product> products = category.getProducts();
            RealmList<Purchase> purchases = new RealmList<>();
            for (Product product : products) {
                Purchase purchase = new Purchase();
                purchase.setProduct(product);
                purchases.add(purchase);
            }
            wrapper.setPurchases(purchases);
            mCategoryList.add(wrapper);
        }
        //mCategoryList.addAll(results);
        mAdapter.notifyParentDataSetChanged(true);

        mAdapter.expandAllParents();

        mAdapter.setOnProductItemChangeListener(productWrapper -> updateTotalPrice());
        updateTotalPrice();
    }

    private RealmList<Purchase> getProductWrapperList() {
        if (mCategoryList == null) return null;

        RealmList<Purchase> list = new RealmList<>();
        for (CategoryWrapper categoryWrapper : mCategoryList) {
            RealmList<Purchase> purchases = categoryWrapper.getPurchases();
            if (purchases == null) return null;
            for (Purchase purchase : purchases) {
                if (purchase.getCount() > 0)
                    list.add(purchase);
            }
        }

        return list;
    }

    // TODO: 2018-02-25 리팩토링
    private void updateTotalPrice() {
        long total = 0;

        RealmList<Purchase> list = getProductWrapperList();
        if (list == null) {
            tvTotalPrice.setText(getString(R.string.format_price, String.valueOf(0)));
            return;
        }
        for (Purchase purchase : list) {
            total += purchase.getCount() * purchase.getProduct().getPrice();
        }
        tvTotalPrice.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(total)));
    }

    // TODO: 2018-02-25 리팩토링
    private void attemptRegister(View view) {
        final RealmList<Purchase> purchaseList = getProductWrapperList();
        if (purchaseList == null || purchaseList.size() == 0) {
            Toast.makeText(this, getString(R.string.error_empty_product_wrapper), Toast.LENGTH_SHORT).show();
            return;
        }

        final Sales sales = new Sales();
        final Date date = new Date(cvCalendar.getDate());
        final String memo = edtMemo.getText().toString();

        sales.setPurchases(purchaseList);
        sales.setSelectedAt(date);
        if (!TextUtils.isEmpty(memo)) sales.setMemo(memo);

        Person person = realm.where(Person.class).equalTo("id", personId).findFirst();
        if (person == null) return;

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(sales);
        // TODO: 2018-06-06 리팩토링 - 생성할떄 정렬하는게 맞나?
        Sales previous = person.getSales().where().lessThan("selectedAt", date).findFirst();
        if (previous != null) person.getSales().add(person.getSales().indexOf(previous), sales);
        else
            person.getSales().add(sales);
        realm.commitTransaction();

        setResult(RESULT_OK);
        finish();
    }
}
