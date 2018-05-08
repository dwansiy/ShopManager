package com.xema.shopmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.model.wrapper.ProductWrapper;
import com.xema.shopmanager.ui.CustomerActivity;
import com.xema.shopmanager.ui.ProfileActivity;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by diygame5 on 2017-09-18.
 * Project : Buyble
 */

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ListItemViewHolder> {
    private static final String TAG = PersonAdapter.class.getSimpleName();

    private Context mContext = null;
    private Realm mRealm = null;
    private List<Person> mDataList = null;

    public PersonAdapter(Context context, Realm realm, List<Person> mDataList) {
        super();
        this.mContext = context;
        this.mRealm = realm;
        this.mDataList = mDataList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        final Person person = mDataList.get(position);

        holder.bind(person, mContext);
    }

    // TODO: 2018-02-15 에러없는지 체크
    @Override
    public long getItemId(int position) {
        return mDataList != null ? (UUID.fromString(mDataList.get(position).getId()).getMostSignificantBits() & Long.MAX_VALUE) : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_container)
        LinearLayout llContainer;
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

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(Person person, Context context) {
            llContainer.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", person.getId());
                if (context instanceof CustomerActivity)
                    ((CustomerActivity) context).startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SALES);
                else context.startActivity(intent);
            });

            String profileImage = person.getProfileImage();
            if (TextUtils.isEmpty(profileImage)) {
                //깜빡임때문에 일단 아이콘으로 해결
                //GlideApp.with(context).load(new ColorDrawable(Color.DKGRAY)).centerCrop().circleCrop().into(ivProfile);
                GlideApp.with(context).load(R.drawable.ic_dark_gray).centerCrop().circleCrop().into(ivProfile);
                tvProfile.setText(person.getName());
            } else {
                GlideApp.with(context).load(profileImage).error(R.drawable.ic_dark_gray).centerCrop().circleCrop().into(ivProfile);
            }

            tvName.setText(person.getName());
            tvPhone.setText(CommonUtil.toHypenFormat(person.getPhone()));
            //tvVisit.setText(String.valueOf(person.getVisit()));

            //Date recentAt = person.getRecentAt();
            //if (recentAt == null) {
            //    tvRecent.setText("X");
            //} else {
            //    tvRecent.setText(CommonUtil.getModifiedDate(person.getRecentAt()));
            //}
            RealmList<Sales> sales = person.getSales();
            if (sales == null || sales.size() == 0) {
                tvRecent.setText("X");
                tvVisit.setText(String.valueOf(0));
                tvTotal.setText(context.getString(R.string.format_price, String.valueOf(0)));
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
                tvTotal.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(total)));
            }


            //tvTotal.setText(context.getString(R.string.format_price, CommonUtil.toDecimalFormat(person.getTotalPrice())));
        }
    }
}
