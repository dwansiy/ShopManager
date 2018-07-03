package com.xema.shopmanager.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.xema.shopmanager.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xema0 on 2018-07-02.
 */

@Deprecated
public abstract class BaseFilterAdapter<T, S extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<S> implements Filterable {
    private List<T> mOriginalArrayList = new ArrayList<>();
    private List<T> mArrayList = new ArrayList<>();
    private RecyclerView recyclerView;

    public void setDataArrayList(List<T> mArrayList) {
        this.mArrayList = mArrayList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void addItem(T object) {
        mOriginalArrayList.add(object);
        notifyDataSetChanged();
    }

    public void setItems(List<T> arrayList) {
        this.mOriginalArrayList.clear();
        this.mOriginalArrayList.addAll(arrayList);
        this.mArrayList.clear();
        this.mArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void addItems(List<T> arrayList) {
        this.mOriginalArrayList.addAll(arrayList);
        this.mArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public List<T> getOriginalArrayList() {
        return mOriginalArrayList;
    }

    public List<T> getList() {
        return mArrayList;
    }

    public T getListItem(int position) {
        if (position > mArrayList.size()) {
            return null;
        }
        return mArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mArrayList.clear();

                if (TextUtils.isEmpty(charSequence)) {
                    mArrayList.addAll(mOriginalArrayList);
                } else {
                    List<T> filteredList = new ArrayList<>();
                    for (T object : mOriginalArrayList) {
                        filterObject(filteredList, object, charSequence.toString());
                    }
                    for (T p : filteredList) {
                        if (p instanceof Person) Log.d("aaaaa", ((Person) p).getName());
                    }
                    mArrayList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                new Handler().postDelayed(() -> {
                    if (recyclerView != null) {
                        recyclerView.getRecycledViewPool().clear();
                    }
                    notifyDataSetChanged();
                }, 20);
            }
        };
    }

    public abstract void filterObject(List<T> filteredList, T object, String searchText);

}