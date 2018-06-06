package com.xema.shopmanager.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.xema.shopmanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleTextDialog extends Dialog {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_negative)
    TextView tvNegative;
    @BindView(R.id.tv_positive)
    TextView tvPositive;

    private Context mContext;
    private OnPositiveListener onPositiveListener;
    private String message;
    private String positiveButtonText;

    public interface OnPositiveListener {
        void onClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_simple_text);
        ButterKnife.bind(this);

        tvMessage.setText(message);
        tvNegative.setOnClickListener(v -> dismiss());
        tvPositive.setText(positiveButtonText);
        tvPositive.setOnClickListener(v -> {
            if (onPositiveListener != null) onPositiveListener.onClick();
            dismiss();
        });
    }

    public SimpleTextDialog(Context context, String message) {
        super(context);
        this.mContext = context;
        this.message = message;
    }

    public void setOnPositiveListener(final String positiveButtonText, OnPositiveListener onPositiveListener) {
        this.positiveButtonText = positiveButtonText;
        this.onPositiveListener = onPositiveListener;
    }
}
