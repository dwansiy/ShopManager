package com.xema.shopmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.CustomerActivity;
import com.xema.shopmanager.ui.CustomerDetailActivity;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.utils.RealmUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by diygame5 on 2017-09-18.
 * Project : Buyble
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ListItemViewHolder> {
    private static final String TAG = CustomerAdapter.class.getSimpleName();

    private Context mContext = null;
    private List<Person> mDataList = null;

    public interface OnDeleteListener {
        void onDelete(Person person, int position);
    }

    public interface OnEditListener {
        void onEdit(Person person, int position);
    }

    private OnDeleteListener onDeleteListener;
    private OnEditListener onEditListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    public CustomerAdapter(Context context, List<Person> personList) {
        super();
        this.mContext = context;
        this.mDataList = personList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ListItemViewHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final Person person = mDataList.get(position);

        holder.bind(mContext, person, position, onDeleteListener, onEditListener);
    }

    @Override
    public long getItemId(int position) {
        if (mDataList == null) return position;

        Person person = mDataList.get(position);
        if (person == null) return position;

        return UUID.fromString(person.getId()).getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile)
        ImageView ivProfile;
        @BindView(R.id.tv_profile)
        TextView tvProfile;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.tv_visit)
        TextView tvVisit;
        @BindView(R.id.tv_recent)
        TextView tvRecent;
        @BindView(R.id.tv_total)
        TextView tvTotal;
        @BindView(R.id.ll_container)
        LinearLayout llContainer;
        @BindView(R.id.iv_edit)
        ImageView ivEdit;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.sml_main)
        SwipeMenuLayout smlMain;

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(Context context, Person person, int position, OnDeleteListener onDeleteListener, OnEditListener onEditListener) {
            llContainer.setOnClickListener(v -> {
                Intent intent = new Intent(context, CustomerDetailActivity.class);
                intent.putExtra("id", person.getId());
                if (context instanceof CustomerActivity)
                    ((CustomerActivity) context).startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SALES);
                else context.startActivity(intent);
            });

            ivDelete.setOnClickListener(v -> {
                if (onDeleteListener != null) onDeleteListener.onDelete(person, position);
            });

            ivEdit.setOnClickListener(v -> {
                if (onEditListener != null) {
                    smlMain.smoothClose();
                    onEditListener.onEdit(person, position);
                }
            });

            String profileImage = person.getProfileImage();
            if (TextUtils.isEmpty(profileImage)) {
                GlideApp.with(context).load(R.drawable.ic_dark_gray).centerCrop().circleCrop().into(ivProfile);
                tvProfile.setText(person.getName());
            } else {
                GlideApp.with(context).load(profileImage).error(R.drawable.ic_dark_gray).centerCrop().circleCrop().into(ivProfile);
            }

            tvName.setText(person.getName());
            tvPhone.setText(CommonUtil.toHypenFormat(person.getPhone()));

            RealmList<Sales> sales = person.getSales();
            if (sales == null || sales.size() == 0) {
                tvRecent.setText("X");
                tvVisit.setText(String.valueOf(0));
                tvTotal.setText(context.getString(R.string.format_price, String.valueOf(0)));
            } else {
                Date recentAt = RealmUtils.getMaxSelectedDate(sales);
                tvRecent.setText(recentAt != null ? CommonUtil.getModifiedDate(recentAt) : "X");
                tvVisit.setText(String.valueOf(sales.size()));

                long total = 0;
                for (Sales item : sales) {
                    RealmList<Purchase> purchases = item.getPurchases();
                    for (Purchase wrapper : purchases) {
                        total += wrapper.getCount() * wrapper.getProduct().getPrice();
                    }
                }
                tvTotal.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(total)));
            }
        }
    }
}
