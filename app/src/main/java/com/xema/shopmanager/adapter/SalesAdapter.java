package com.xema.shopmanager.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.wrapper.ProductWrapper;
import com.xema.shopmanager.ui.dialog.MemoDialog;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by xema0 on 2018-02-25.
 */

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ListItemViewHolder> {
    private static final String TAG = SalesAdapter.class.getSimpleName();

    private Context mContext = null;
    private Realm mRealm = null;
    private List<Sales> mDataList = null;

    public SalesAdapter(Context context, Realm realm, List<Sales> mDataList) {
        super();
        this.mContext = context;
        this.mRealm = realm;
        this.mDataList = mDataList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final Sales sales = mDataList.get(position);

        holder.bind(sales, mContext);
    }

    @Override
    public long getItemId(int position) {
        return mDataList != null ? (UUID.fromString(mDataList.get(position).getId()).getMostSignificantBits() & Long.MAX_VALUE) : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.iv_memo)
        ImageView ivMemo;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(Sales sales, Context context) {
            final String memo = sales.getMemo();
            if (TextUtils.isEmpty(memo)) ivMemo.setVisibility(View.GONE);
            else ivMemo.setVisibility(View.VISIBLE);
            // TODO: 2018-02-25 메모내용 다이얼로그 띄우기

            final Date date = sales.getSelectedAt();
            tvDate.setText(CommonUtil.getModifiedDate(date));

            RealmList<ProductWrapper> productWrappers = sales.getProductWrappers();
            StringBuilder nameBuilder = new StringBuilder();
            String delim = "";
            long price = 0;
            for (ProductWrapper productWrapper : productWrappers) {
                if (productWrapper.getCount() > 0) {
                    nameBuilder.append(delim).append(productWrapper.getProduct().getName());
                    delim = ", ";
                    price += productWrapper.getCount() * productWrapper.getProduct().getPrice();
                }
            }
            tvName.setText(nameBuilder.toString());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(price)));

            ivMemo.setOnClickListener(v -> {
                Dialog dialog = new MemoDialog(context, date, memo);
                dialog.show();
            });
        }
    }
}
