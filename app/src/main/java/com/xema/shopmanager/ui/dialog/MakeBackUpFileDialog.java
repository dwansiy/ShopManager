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
 * Created by xema0 on 2018-08-24.
 */

public class MakeBackUpFileDialog extends Dialog {
    @BindView(R.id.edt_file_name)
    EditText edtFileName;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_register)
    TextView tvRegister;

    private OnRegisterListener listener;
    private String mFileName;

    public MakeBackUpFileDialog(@NonNull Context context, String fileName) {
        super(context);
        this.mFileName = fileName;
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

        setContentView(R.layout.dialog_make_back_up_file);
        ButterKnife.bind(this);

        edtFileName.setText(mFileName);
        
        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) listener.onRegister(edtFileName.getText().toString());
            dismiss();
        });

        edtFileName.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    if (listener != null) listener.onRegister(edtFileName.getText().toString());
                    break;
                default:
                    return false;
            }
            return true;
        });

        edtFileName.requestFocus();
        CommonUtil.showKeyBoard(getContext(), edtFileName);
    }
}
