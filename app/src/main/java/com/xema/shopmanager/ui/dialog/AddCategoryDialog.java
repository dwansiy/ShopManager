package com.xema.shopmanager.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class AddCategoryDialog extends Dialog {
    @BindView(R.id.edt_category)
    EditText edtCategory;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_register)
    TextView tvRegister;

    private OnRegisterListener listener;

    public AddCategoryDialog(@NonNull Context context) {
        super(context);
    }

    public interface OnRegisterListener {
        void onRegister(String name);
    }

    public void setListener(OnRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_category);
        ButterKnife.bind(this);

        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) listener.onRegister(edtCategory.getText().toString());
            dismiss();
        });

        edtCategory.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    if (listener != null) listener.onRegister(edtCategory.getText().toString());
                    break;
                default:
                    return false;
            }
            return true;
        });

        edtCategory.requestFocus();
        CommonUtil.showKeyBoard(getContext(), edtCategory);
    }
}
