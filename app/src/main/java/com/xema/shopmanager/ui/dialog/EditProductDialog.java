package com.xema.shopmanager.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class EditProductDialog extends Dialog {
    @BindView(R.id.edt_product_name)
    EditText edtProductName;
    @BindView(R.id.edt_product_price)
    EditText edtProductPrice;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_register)
    TextView tvRegister;

    private Context mContext;
    private OnRegisterListener listener;

    private String name;
    private long price;

    public EditProductDialog(@NonNull Context context, String name, long price) {
        super(context);
        mContext = context;
        this.name = name;
        this.price = price;
    }

    public interface OnRegisterListener {
        void onRegister(String name, long price);
    }

    public void setListener(OnRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_add_product);
        ButterKnife.bind(this);

        edtProductName.setText(name);
        edtProductPrice.setText(String.valueOf(price));

        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) {
                String name = edtProductName.getText().toString();
                String price = edtProductPrice.getText().toString();
                if (!TextUtils.isEmpty(price) && TextUtils.isDigitsOnly(price)) {
                    listener.onRegister(name, Long.parseLong(price));
                    dismiss();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.message_error_no_input_price), Toast.LENGTH_SHORT).show();
                }
            } else {
                dismiss();
            }
        });
    }
}
