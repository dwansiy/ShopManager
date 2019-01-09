package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.CustomerAdapter;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchActivity extends AppCompatActivity implements Filter.FilterListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.abl_main)
    AppBarLayout ablMain;
    @BindView(R.id.tv_search_count)
    TextView tvSearchCount;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;

    private Realm realm;
    private CustomerAdapter mAdapter;
    private RealmAsyncTask transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initToolbar();
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
    }

    private void initListeners() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                attemptSearch(editable.toString());
            }
        });
        srlMain.setOnRefreshListener(() -> {
            attemptSearch(edtSearch.getText().toString());
        });
    }

    private void attemptSearch(String s) {
        if (mAdapter == null) return;
        mAdapter.getFilter().filter(s, this); //Async
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
        RealmQuery<Person> q = realm.where(Person.class).sort("name", Sort.ASCENDING);
        return q.findAll();
    }

    //Show empty view if needed, Change toolbar status
    private void updateUI() {
        if (mAdapter == null) return;

        boolean isInSearchMode = edtSearch.getText().length() != 0;
        int size = mAdapter.getData() == null ? 0 : mAdapter.getData().size();

        if (isInSearchMode) {
            tvSearchCount.setText(getString(R.string.format_search_customer, size));
        } else {
            tvSearchCount.setText(getString(R.string.format_count_customer, size));
        }
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

    private void showDeleteDialog(Person person, int position) {
        SimpleTextDialog dialog = new SimpleTextDialog(this, getString(R.string.alert_delete_customer, person.getName()));
        dialog.setOnPositiveListener(this.getString(R.string.common_delete), () -> {
            deletePerson(person, position);
        });
        dialog.show();
    }

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
            Toast.makeText(SearchActivity.this, getString(R.string.message_delete_success), Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(SearchActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show());
    }

    private void attemptEdit(Person person, int position) {
        Intent intent = new Intent(this, EditCustomerActivity.class);
        intent.putExtra("personId", person.getId());
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public void onFilterComplete(int count) {
        updateUI();
    }
}
