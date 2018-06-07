package com.xema.shopmanager.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.xema.shopmanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeleteEditBottomSheetDialog extends BottomSheetDialog {
    @BindView(R.id.ll_edit)
    LinearLayout llEdit;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;

    private OnDeleteListener onDeleteListener;
    private OnEditListener onEditListener;

    public interface OnDeleteListener {
        void onDelete();
    }

    public interface OnEditListener {
        void onEdit();
    }

    public DeleteEditBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet_delete_edit, null);

        setContentView(contentView);
        configureBottomSheetBehavior(contentView);
        ButterKnife.bind(this);
        llDelete.setOnClickListener(v -> {
            if (onDeleteListener != null) onDeleteListener.onDelete();
        });
        llEdit.setOnClickListener(v -> {
            if (onEditListener != null) onEditListener.onEdit();
        });
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    public void setOnEditListener(OnEditListener listener) {
        this.onEditListener = listener;
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
