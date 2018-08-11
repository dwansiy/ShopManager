package com.xema.shopmanager.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xema.shopmanager.R;

@Deprecated
public class QuickPanelTipView extends RelativeLayout {
    private TextView mTipsView;

    public QuickPanelTipView(Context context) {
        this(context, null);
        init(context,null);
    }

    public QuickPanelTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context,attrs);
    }

    public QuickPanelTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTipsView = new TextView(context);
        mTipsView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textSize_quicksidebartips));
        mTipsView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTipsView.setLayoutParams(layoutParams);
        addView(mTipsView, layoutParams);
    }

    public void setText(String text, int poistion, float y) {
        mTipsView.setText(text);
        mTipsView.invalidate();
        //LayoutParams layoutParams = (LayoutParams) mTipsView.getLayoutParams();
        //layoutParams.topMargin = (int)(y - getWidth()/2.8);
        //mTipsView.setLayoutParams(layoutParams);
    }


}