package com.xema.shopmanager.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.ui.SalesRegisterActivity;
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

    public interface OnDeleteListener {
        void onDelete(Sales sales, int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final Sales sales = mDataList.get(position);

        holder.bind(mContext, sales, position, onDeleteListener);
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

        private void bind(Context context, Sales sales, int position, OnDeleteListener onDeleteListener) {
            final String memo = sales.getMemo();
            if (TextUtils.isEmpty(memo)) ivMemo.setVisibility(View.GONE);
            else ivMemo.setVisibility(View.VISIBLE);
            // TODO: 2018-02-25 메모내용 다이얼로그 띄우기

            final Date date = sales.getSelectedAt();
            tvDate.setText(CommonUtil.getModifiedDate(date));
            ivMemo.setOnClickListener(v -> {
                Dialog dialog = new MemoDialog(context, date, memo);
                dialog.show();
            });

            RealmList<Purchase> purchases = sales.getPurchases();
            if (purchases.size() == 0) {
                tvName.setText(context.getString(R.string.message_deleted_product));
                tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(0)));
                return;
            }

            StringBuilder nameBuilder = new StringBuilder();
            String delimeter = "";
            long price = 0;
            for (Purchase purchase : purchases) {
                if (purchase.getCount() > 0) {
                    nameBuilder.append(delimeter).append(purchase.getProduct().getName());
                    delimeter = ", ";
                    price += purchase.getCount() * purchase.getProduct().getPrice();
                }
            }
            tvName.setText(nameBuilder.toString());
            tvPrice.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(price)));

            Person person = sales.getPerson().first();
            if (person == null) return;
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SalesRegisterActivity.class);
                intent.putExtra("personId", person.getId());
                intent.putExtra("salesId", sales.getId());
                ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_SALES);
            });
            itemView.setOnLongClickListener(v -> {
                PopupMenu p = new PopupMenu(context, v);
                if (!(context instanceof Activity)) return false;
                ((Activity) context).getMenuInflater().inflate(R.menu.menu_edit_delete, p.getMenu());
                p.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        Intent intent = new Intent(context, SalesRegisterActivity.class);
                        intent.putExtra("personId", person.getId());
                        intent.putExtra("salesId", sales.getId());
                        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_SALES);
                    } else if (item.getItemId() == R.id.menu_delete) {
                        if (onDeleteListener != null)
                            onDeleteListener.onDelete(sales, position);
                    }
                    return false;
                });
                p.show();
                return false;
            });
        }
    }
}
