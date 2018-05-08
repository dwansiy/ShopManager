package com.xema.shopmanager.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.utils.CommonUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-02-19.
 */

public class MemoDialog extends Dialog {

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_memo)
    TextView tvMemo;

    private Date date;
    private String memo;

    public MemoDialog(@NonNull Context context, Date date, String memo) {
        super(context);
        this.date = date;
        this.memo = memo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_memo);
        ButterKnife.bind(this);

        tvDate.setText(CommonUtil.getModifiedDate(date));
        tvMemo.setText(memo);
    }
}
