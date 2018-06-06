package com.xema.shopmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class CategoryAdapter extends ExpandableRecyclerAdapter<Category, Product, CategoryAdapter.CategoryViewHolder, CategoryAdapter.ProductViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private Context mContext;

    private OnProductLongClickListener onProductLongClickListener;

    public interface OnProductLongClickListener {
        void onProductLongClick(Product product);
    }

    public void setOnProductLongClickListener(OnProductLongClickListener onProductLongClickListener) {
        this.onProductLongClickListener = onProductLongClickListener;
    }

    private OnCategoryLongClickListener onCategoryLongClickListener;

    public interface OnCategoryLongClickListener {
        void onCategoryLongClick(Category category);
    }

    public void setOnCategoryLongClickListener(OnCategoryLongClickListener onCategoryLongClickListener) {
        this.onCategoryLongClickListener = onCategoryLongClickListener;
    }

    private OnAddProductListener onAddProductListener;

    public interface OnAddProductListener {
        void onAddProduct(Category category);
    }

    public void setOnAddProductListener(OnAddProductListener onAddProductListener) {
        this.onAddProductListener = onAddProductListener;
    }

    public CategoryAdapter(Context context, @NonNull List<Category> categoryList) {
        super(categoryList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View categoryView = mInflater.inflate(R.layout.item_category, parentViewGroup, false);
        return new CategoryViewHolder(categoryView);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View productView = mInflater.inflate(R.layout.item_product, childViewGroup, false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int parentPosition, @NonNull Category category) {
        categoryViewHolder.bind(mContext, category, onAddProductListener, onCategoryLongClickListener);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProductViewHolder productViewHolder, int parentPosition, int childPosition, @NonNull Product product) {
        productViewHolder.bind(mContext, product, onProductLongClickListener);
    }

    final static class CategoryViewHolder extends ParentViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_add_product)
        LinearLayout llAddProduct;
        @BindView(R.id.ll_background)
        LinearLayout llBackground;
        @BindView(R.id.iv_fold)
        ImageView ivFold;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llBackground.setOnClickListener(v -> {
                if (isExpanded()) collapseView();
                else expandView();
            });
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (isExpanded()) ivFold.setRotation(0f);
            else ivFold.setRotation(180f);
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);

            RotateAnimation rotateAnimation = new RotateAnimation(180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setFillAfter(true);
            ivFold.startAnimation(rotateAnimation);
        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }

        void bind(Context context, Category category, OnAddProductListener onAddProductListener, OnCategoryLongClickListener onDeleteCategoryListener) {
            tvName.setText(category.getName());
            llAddProduct.setOnClickListener(v -> {
                if (onAddProductListener != null) onAddProductListener.onAddProduct(category);
            });
            llBackground.setOnLongClickListener(v -> {
                if (onDeleteCategoryListener != null)
                    onDeleteCategoryListener.onCategoryLongClick(category);
                return false;
            });
        }
    }

    final static class ProductViewHolder extends ChildViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.ll_background)
        LinearLayout llBackground;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Context context, Product product, OnProductLongClickListener onProductLongClickListener) {
            tvName.setText(product.getName());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(product.getPrice())));
            llBackground.setOnLongClickListener(v -> {
                if (onProductLongClickListener != null)
                    onProductLongClickListener.onProductLongClick(product);
                return false;
            });
        }
    }
}