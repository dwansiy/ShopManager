package com.xema.shopmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Chart;
import com.xema.shopmanager.model.Product;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class ChartAdapter extends ExpandableRecyclerAdapter<Chart, Purchase, ChartAdapter.ChartViewHolder, ChartAdapter.ProductViewHolder> {
    private static final String TAG = ChartAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    private Context mContext;

    public ChartAdapter(Context context, @NonNull List<Chart> categoryList) {
        super(categoryList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_chart_parent, parentViewGroup, false);
        return new ChartViewHolder(view);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_chart_child, childViewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(@NonNull ChartViewHolder chartViewHolder, int parentPosition, @NonNull Chart chart) {
        chartViewHolder.bind(mContext, chart);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProductViewHolder productViewHolder, int parentPosition, int childPosition, @NonNull Purchase purchase) {
        productViewHolder.bind(mContext, purchase);
    }

    final static class ChartViewHolder extends ParentViewHolder {
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        ChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Context context, Chart chart) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(chart.getDate());

            tvDate.setText(context.getString(R.string.format_date, calendar.get(Calendar.DAY_OF_MONTH), calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())));
            List<Purchase> purchases = chart.getPurchases();
            long price = 0;
            if (purchases == null || purchases.isEmpty()) {
                tvCount.setText(context.getString(R.string.format_count, 0));
            } else {
                tvCount.setText(context.getString(R.string.format_count, purchases.size()));
                for (Purchase purchase : purchases) {
                    price += purchase.getCount() * purchase.getProduct().getPrice();
                }
            }
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(price)));
        }
    }

    final class ProductViewHolder extends ChildViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_count)
        TextView tvCount;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Context context, Purchase purchase) {
            Product product = purchase.getProduct();

            tvName.setText(product.getName());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(product.getPrice())));

            tvCount.setText(String.valueOf(purchase.getCount()));
        }
    }
}