package com.xema.shopmanager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.xema.shopmanager.R;

/**
 * Created by xema0 on 2018-02-10.
 */

public class FontTextView extends android.support.v7.widget.AppCompatTextView {

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }

    }

    public FontTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontTextView);
            try {
                if (a.getString(R.styleable.FontTextView_font_name) != null) {
                    String fontName = a.getString(R.styleable.FontTextView_font_name);
                    if (fontName != null) {
                        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/" + fontName);
                        setTypeface(myTypeface);
                    }
                    a.recycle();
                }

            } catch (Exception e) {
                //
            }
        }
    }
}