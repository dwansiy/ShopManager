package com.xema.shopmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Category;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.model.wrapper.CategoryWrapper;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class SalesRegisterAdapter extends ExpandableRecyclerAdapter<CategoryWrapper, Purchase, SalesRegisterAdapter.CategoryViewHolder, SalesRegisterAdapter.ProductViewHolder> {
    private static final String TAG = SalesRegisterAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    private Context mContext;

    private OnProductItemChangeListener onProductItemChangeListener;

    public interface OnProductItemChangeListener {
        public void onProductItemChange(Purchase purchase);
    }

    public void setOnProductItemChangeListener(OnProductItemChangeListener onProductItemChangeListener) {
        this.onProductItemChangeListener = onProductItemChangeListener;
    }

    public SalesRegisterAdapter(Context context, @NonNull List<CategoryWrapper> categoryList) {
        super(categoryList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView = mInflater.inflate(R.layout.item_category_sales, parentViewGroup, false);
        return new CategoryViewHolder(recipeView);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView = mInflater.inflate(R.layout.item_product_sales, childViewGroup, false);
        return new ProductViewHolder(ingredientView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int parentPosition, @NonNull CategoryWrapper category) {
        categoryViewHolder.bind(mContext, category);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProductViewHolder productViewHolder, int parentPosition, int childPosition, @NonNull Purchase purchase) {
        productViewHolder.bind(mContext, purchase, onProductItemChangeListener);
    }

    final static class CategoryViewHolder extends ParentViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_fold)
        ImageView ivFold;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
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

        void bind(Context context, CategoryWrapper wrapper) {
            Category category = wrapper.getCategory();
            tvName.setText(category.getName());
        }
    }

    final class ProductViewHolder extends ChildViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.iv_minus)
        ImageView ivMinus;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.iv_plus)
        ImageView ivPlus;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Context context, Purchase purchase, OnProductItemChangeListener onProductItemChangeListener) {
            Product product = purchase.getProduct();

            tvName.setText(product.getName());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(product.getPrice())));

            tvCount.setText(String.valueOf(purchase.getCount()));

            ivMinus.setOnClickListener(v -> {
                int count = purchase.getCount();
                if (count <= 0) return;
                count--;
                purchase.setCount(count);
                if (onProductItemChangeListener != null)
                    onProductItemChangeListener.onProductItemChange(purchase);
                tvCount.setText(String.valueOf(count));
            });

            ivPlus.setOnClickListener(v -> {
                int count = purchase.getCount();
                if (count > 1000) return;
                count++;
                purchase.setCount(count);
                if (onProductItemChangeListener != null)
                    onProductItemChangeListener.onProductItemChange(purchase);
                tvCount.setText(String.valueOf(count));
            });
        }
    }
}