package com.xema.shopmanager.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.Switch;

import com.xema.shopmanager.R;
import com.xema.shopmanager.common.Constants;
import com.xema.shopmanager.common.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: 2018-08-04
/* 관리 객체에서는 CUSTOM SORT 불가능... PRICE, VISIT, RECENT 정렬은 Person 객체에 필드 따로 빼서 구현하는식으로 할것 */
public class SortBottomSheetDialog extends BottomSheetDialog {

    @BindView(R.id.tv_sort_name)
    CheckedTextView tvSortName;
    @BindView(R.id.tv_sort_price)
    CheckedTextView tvSortPrice;
    @BindView(R.id.tv_sort_visit)
    CheckedTextView tvSortVisit;
    @BindView(R.id.tv_sort_create)
    CheckedTextView tvSortCreate;
    @BindView(R.id.tv_sort_recent)
    CheckedTextView tvSortRecent;

    private onSortListener onSortListener;

    private Context mContext;

    public interface onSortListener {
        void onSort(Constants.Sort sort);
    }

    public void setOnSortListener(onSortListener listener) {
        this.onSortListener = listener;
    }

    public SortBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet_sort, null);

        setContentView(contentView);
        configureBottomSheetBehavior(contentView);
        ButterKnife.bind(this);

        resetCheck();
        switch (PreferenceHelper.loadSortMode(mContext)) {
            case NAME:
                tvSortName.setChecked(true);
                break;
            case RECENT:
                tvSortRecent.setChecked(true);
                break;
            case PRICE:
                tvSortPrice.setChecked(true);
                break;
            case VISIT:
                tvSortVisit.setChecked(true);
                break;
            case CREATE:
                tvSortCreate.setChecked(true);
                break;
            default:
                tvSortName.setChecked(true);
                break;
        }

        View.OnClickListener onClickListener = v -> {
            if (onSortListener == null) return;
            resetCheck();
            if (v instanceof CheckedTextView) ((CheckedTextView) v).setChecked(true);

            switch (v.getId()) {
                case R.id.tv_sort_name:
                    onSortListener.onSort(Constants.Sort.NAME);
                    break;
                case R.id.tv_sort_price:
                    onSortListener.onSort(Constants.Sort.PRICE);
                    break;
                case R.id.tv_sort_visit:
                    onSortListener.onSort(Constants.Sort.VISIT);
                    break;
                case R.id.tv_sort_create:
                    onSortListener.onSort(Constants.Sort.CREATE);
                    break;
                case R.id.tv_sort_recent:
                    onSortListener.onSort(Constants.Sort.RECENT);
                    break;
                default:
                    break;

            }
            dismiss();
        };

        tvSortName.setOnClickListener(onClickListener);
        tvSortPrice.setOnClickListener(onClickListener);
        tvSortVisit.setOnClickListener(onClickListener);
        tvSortCreate.setOnClickListener(onClickListener);
        tvSortRecent.setOnClickListener(onClickListener);
    }

    private void resetCheck() {
        tvSortName.setChecked(false);
        tvSortPrice.setChecked(false);
        tvSortVisit.setChecked(false);
        tvSortCreate.setChecked(false);
        tvSortRecent.setChecked(false);
    }

    private void configureBottomSheetBehavior(View contentView) {
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) contentView.getParent());

        if (behavior != null) {
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            dismiss();
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }
}
