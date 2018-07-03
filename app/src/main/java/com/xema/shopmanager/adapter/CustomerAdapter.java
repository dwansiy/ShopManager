package com.xema.shopmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.CustomerActivity;
import com.xema.shopmanager.ui.ProfileActivity;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.utils.RealmUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by diygame5 on 2017-09-18.
 * Project : Buyble
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ListItemViewHolder> {
    private static final String TAG = CustomerAdapter.class.getSimpleName();

    private Context mContext = null;
    private List<Person> mDataList = null;
    private Realm realm;

    public CustomerAdapter(Context context, List<Person> personList, Realm realm) {
        super();
        this.mContext = context;
        this.mDataList = personList;
        this.realm = realm;
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

        holder.bind(person, mContext);
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

    //@Override
    //public void filterObject(List<Person> filteredList, Person person, String searchText) {
    //    String name = person.getName();
    //    String phone = person.getPhone();
    //    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
    //        if (name.contains(searchText) || phone.contains(searchText)) {
    //            filteredList.add(person);
    //            return;
    //        }
    //    }
    //    if (!TextUtils.isEmpty(phone)) {
    //        if (phone.contains(searchText)) {
    //            filteredList.add(person);
    //            return;
    //        }
    //    }
    //    if (!TextUtils.isEmpty(name)) {
    //        if (name.contains(searchText)) {
    //            filteredList.add(person);
    //        }
    //    }
    //}

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
