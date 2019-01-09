package com.xema.shopmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.GlideApp;
import com.xema.shopmanager.common.PreferenceHelper;
import com.xema.shopmanager.enums.SortType;
import com.xema.shopmanager.model.Person;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.ui.CustomerDetailActivity;
import com.xema.shopmanager.ui.CustomerActivity;
import com.xema.shopmanager.ui.SearchActivity;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.utils.RealmUtils;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class CustomerAdapter extends RealmRecyclerViewAdapter<Person, CustomerAdapter.ListItemViewHolder> implements Filterable {
    private static final String TAG = CustomerAdapter.class.getSimpleName();
    private Context mContext = null;
    private Realm realm;

    private OnDeleteListener onDeleteListener;
    private OnEditListener onEditListener;

    public interface OnDeleteListener {
        void onDelete(Person person, int position);
    }

    public interface OnEditListener {
        void onEdit(Person person, int position);
    }

    public CustomerAdapter(Context context, Realm realm, OrderedRealmCollection<Person> data) {
        super(data, true);
        this.mContext = context;
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
        final Person person = getItem(position);
        holder.bind(mContext, person, position, onDeleteListener, onEditListener);
    }

    @Override
    public long getItemId(int position) {
        Person person = getItem(position);
        if (person == null) return position;
        return UUID.fromString(person.getId()).getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Override
    public int getItemCount() {
        return getData() == null ? 0 : getData().size();
    }

    public void refresh() {
        SortType sort = PreferenceHelper.loadSortMode(mContext);
        if (sort == null) {
            updateData(realm.where(Person.class).sort("name", Sort.ASCENDING).findAll()); //error
            return;
        }

        if (sort == SortType.NAME) {
            updateData(realm.where(Person.class).sort("name", Sort.ASCENDING).findAll());
        } else if (sort == SortType.CREATE) {
            updateData(realm.where(Person.class).sort("createdAt", Sort.DESCENDING).findAll());
        }
    }

    private void filterResults(String text, SortType sort) {
        if (TextUtils.isEmpty(text)) {
            if (sort == SortType.NAME) {
                updateData(realm.where(Person.class).sort("name", Sort.ASCENDING).findAll());
                return;
            } else if (sort == SortType.CREATE) {
                updateData(realm.where(Person.class).sort("createdAt", Sort.DESCENDING).findAll());
                return;
            }
        } else {
            if (sort == SortType.NAME) {
                updateData(realm.where(Person.class).contains("name", text, Case.INSENSITIVE).or().contains("phone", text, Case.INSENSITIVE).sort("name", Sort.ASCENDING).findAll());
                return;
            } else if (sort == SortType.CREATE) {
                updateData(realm.where(Person.class).contains("name", text, Case.INSENSITIVE).or().contains("phone", text, Case.INSENSITIVE).sort("createdAt", Sort.DESCENDING).findAll());
                return;
            }
        }
        updateData(realm.where(Person.class).sort("name", Sort.ASCENDING).findAll()); //error
    }

    public Filter getFilter() {
        return new SearchFilter(this);
    }

    private class SearchFilter extends Filter {
        private final CustomerAdapter adapter;

        private SearchFilter(CustomerAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //SEARCH ACTIVITY 분리해서 이름순으로만 정렬되게끔 검색하도록 변경
            //adapter.filterResults(constraint.toString(), PreferenceHelper.loadSortMode(mContext));
            adapter.filterResults(constraint.toString(), SortType.NAME);
        }
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        private void bind(Context context, Person person, int position, OnDeleteListener onDeleteListener, OnEditListener onEditListener) {
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CustomerDetailActivity.class);
                intent.putExtra("id", person.getId());
                if (context instanceof CustomerActivity)
                    ((CustomerActivity) context).startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SALES);
                else context.startActivity(intent);
            });
            itemView.setOnLongClickListener(v -> {
                PopupMenu p = new PopupMenu(context, v);
                if (!(context instanceof Activity)) return false;
                ((Activity) context).getMenuInflater().inflate(R.menu.menu_edit_delete, p.getMenu());
                p.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        if (onEditListener != null) onEditListener.onEdit(person, position);
                    } else if (item.getItemId() == R.id.menu_delete) {
                        if (onDeleteListener != null) onDeleteListener.onDelete(person, position);
                    }
                    return false;
                });
                p.show();
                return false;
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

        @Override
        public void onClick(View v) {

        }
    }
}
