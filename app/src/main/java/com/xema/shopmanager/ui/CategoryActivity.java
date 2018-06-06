package com.xema.shopmanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.CategoryAdapter;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.ui.dialog.AddCategoryDialog;
import com.xema.shopmanager.ui.dialog.AddProductDialog;
import com.xema.shopmanager.ui.dialog.DeleteEditBottomSheetDialog;
import com.xema.shopmanager.ui.dialog.EditCategoryDialog;
import com.xema.shopmanager.ui.dialog.SimpleTextDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

/**
 * Created by xema0 on 2018-02-19.
 */

public class CategoryActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;

    private List<Category> mCategoryList;
    private CategoryAdapter mAdapter;

    private Realm realm;
    private RealmAsyncTask transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        initAdapter();

        queryCategory();
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
        ivAdd.setOnClickListener(this::attemptAddCategory);
    }

    private void initAdapter() {
        mCategoryList = new ArrayList<>();
        mAdapter = new CategoryAdapter(this, mCategoryList);
        mAdapter.setOnAddProductListener(this::attemptAddProduct);
        mAdapter.setOnCategoryLongClickListener(this::attemptCategoryLongClick);
        rvMain.setAdapter(mAdapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
    }

    private void queryCategory() {
        RealmResults<Category> results = realm.where(Category.class).findAll();

        if (results == null || results.size() == 0) {
            rvMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);

            if (mCategoryList != null) {
                mCategoryList.clear();
            }
            else mCategoryList = new ArrayList<>();

            mCategoryList.addAll(results);
            mAdapter.notifyParentDataSetChanged(true);

            mAdapter.expandAllParents();
        }
    }

    private void attemptAddCategory(View view) {
        view.setEnabled(false);

        AddCategoryDialog dialog = new AddCategoryDialog(this);
        dialog.setListener(this::addCategory);
        dialog.show();
        view.setEnabled(true);
    }

    private void addCategory(String name) {
        if (realm == null) return;

        final Category category = new Category();
        category.setName(name);

        transaction = realm.executeTransactionAsync(bgRealm -> {
            bgRealm.copyToRealm(category);
        }, this::queryCategory, error -> {
            //error.printStackTrace();
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptEditCategory(Category category) {
        EditCategoryDialog dialog = new EditCategoryDialog(this, category.getName());
        dialog.setListener(name -> editCategory(category, name));
        dialog.show();
    }

    private void editCategory(final Category original, String name) {
        if (realm == null) return;

        final String id = original.getId();
        transaction = realm.executeTransactionAsync(bgRealm -> {
            Category category = bgRealm.where(Category.class).equalTo("id", id).findFirst();
            if (category == null) return;
            category.setName(name);
            bgRealm.copyToRealmOrUpdate(category);
        }, this::queryCategory, error -> {
            //error.printStackTrace();
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptAddProduct(Category category) {
        AddProductDialog dialog = new AddProductDialog(this);
        dialog.setListener((name, price) -> {
            addProduct(category, name, price);
        });
        dialog.show();
    }

    private void addProduct(Category category, String name, long price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        final String id = category.getId();

        // TODO: 2018-02-19 리팩토링 필요 (쿼리 한번더하지말고 쓰레드간에 전달할수있는방법 찾기... rx 라던지?)
        transaction = realm.executeTransactionAsync(bgRealm -> {
            Category results = bgRealm.where(Category.class).equalTo("id", id).findFirst();
            if (results == null) return;
            results.getProducts().add(product);
            //bgRealm.copyToRealmOrUpdate(results);
        }, this::queryCategory, error -> {
            error.printStackTrace();
            Toast.makeText(this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptCategoryLongClick(Category category) {
        DeleteEditBottomSheetDialog dialog = new DeleteEditBottomSheetDialog(this, R.style.FullScreenTransparentDialog);
        dialog.setOnDeleteListener(() -> {
            dialog.dismiss();
            showDeleteCategoryDialog(category);
        });
        dialog.setOnEditListener(() -> {
            dialog.dismiss();
            attemptEditCategory(category);
            // TODO: 2018-06-06
        });
        dialog.show();
    }

    private void showDeleteCategoryDialog(Category category) {
        SimpleTextDialog dialog = new SimpleTextDialog(this, getString(R.string.alert_delete_category, category.getName()));
        dialog.setOnPositiveListener(this.getString(R.string.common_delete), () -> {
            deleteCategory(category);
        });
        dialog.show();
    }

    private void deleteCategory(Category category) {
        final String id = category.getId();

        realm.executeTransactionAsync(realm -> {
            Category deleteCategory = realm.where(Category.class).equalTo("id", id).findFirst();
            if (deleteCategory == null) return;
            //하위 판매상품도 삭제하게 될경우 cascade delete 때문에 너무 복잡해짐, 기획적으로도 문제됨 (예전에 등록한 판매목록을 카테고리를 삭제한다고 삭제해도되나?)
            //deleteCategory.getProducts().deleteAllFromRealm();
            deleteCategory.deleteFromRealm();
        }, this::queryCategory);
    }
}
