package com.xema.shopmanager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xema.shopmanager.R;

import java.util.Arrays;
import java.util.List;

public class QuickPanelView extends View {

    private OnQuickSideBarTouchListener listener;
    private List<String> mLetters;
    private int mChoose = -1;
    private Paint mPaint = new Paint();
    private float mTextSize;
    private float mTextSizeChoose;
    private int mTextColor;
    private int mTextColorChoose;
    private int mWidth;
    private int mHeight;
    private float mItemHeight;
    private float mItemStartY;
    private Rect rect = new Rect();

    public QuickPanelView(Context context) {
        this(context, null);
    }

    public QuickPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLetters = Arrays.asList(context.getResources().getStringArray(R.array.quick_panel_letters));

        mTextColor = context.getResources().getColor(android.R.color.black);
        mTextColorChoose = context.getResources().getColor(android.R.color.black);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.textSize_quick_panel);
        mTextSizeChoose = context.getResources().getDimensionPixelSize(R.dimen.textSize_quick_panel_choose);
        mItemHeight = context.getResources().getDimension(R.dimen.height_quick_panel_item);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.QuickPanelView);

            mTextColor = a.getColor(R.styleable.QuickPanelView_sidebarTextColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.QuickPanelView_sidebarTextColorChoose, mTextColorChoose);
            mTextSize = a.getDimension(R.styleable.QuickPanelView_sidebarTextSize, mTextSize);
            mTextSizeChoose = a.getDimension(R.styleable.QuickPanelView_sidebarTextSizeChoose, mTextSizeChoose);
            mItemHeight = a.getDimension(R.styleable.QuickPanelView_sidebarItemHeight, mItemHeight);
            a.recycle();
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mLetters.size(); i++) {
            mPaint.setColor(mTextColor);

            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            if (i == mChoose) {
                mPaint.setColor(mTextColorChoose);
                mPaint.setFakeBoldText(true);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setTextSize(mTextSizeChoose);
            }

            mPaint.getTextBounds(mLetters.get(i), 0, mLetters.get(i).length(), rect);
            float xPos = (int) ((mWidth - rect.width()) * 0.5);
            float yPos = mItemHeight * i + (int) ((mItemHeight - rect.height()) * 0.5) + mItemStartY;

            canvas.drawText(mLetters.get(i), xPos, yPos, mPaint);
            mPaint.reset();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mItemStartY = (mHeight - mLetters.size() * mItemHeight) / 2;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final int newChoose = (int) ((y - mItemStartY) / mItemHeight);
        switch (action) {
            case MotionEvent.ACTION_UP:
                mChoose = -1;
                if (listener != null) {
                    listener.onLetterTouching(false);
                }
                invalidate();
                break;
            default:
                if (oldChoose != newChoose) {
                    if (newChoose >= 0 && newChoose < mLetters.size()) {
                        mChoose = newChoose;
                        if (listener != null) {
                            Rect rect = new Rect();
                            mPaint.getTextBounds(mLetters.get(mChoose), 0, mLetters.get(mChoose).length(), rect);
                            float yPos = mItemHeight * mChoose + (int) ((mItemHeight - rect.height()) * 0.5) + mItemStartY;
                            listener.onLetterChanged(mLetters.get(newChoose), mChoose, yPos);
                        }
                    }
                    invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (listener != null) {
                        listener.onLetterTouching(false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (listener != null) {
                        listener.onLetterTouching(true);
                    }
                }

                break;
        }
        return true;
    }

    public OnQuickSideBarTouchListener getListener() {
        return listener;
    }

    public void setOnQuickSideBarTouchListener(OnQuickSideBarTouchListener listener) {
        this.listener = listener;
    }

    public List<String> getLetters() {
        return mLetters;
    }

    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        invalidate();
    }

    public interface OnQuickSideBarTouchListener {
        void onLetterChanged(String letter, int position, float y);

        void onLetterTouching(boolean touching);
    }
}

