package com.xema.shopmanager.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xema.shopmanager.R;

import java.util.Calendar;

/**
 * Created by xema0 on 2018-03-10.
 */

public class MonthCalendarView extends LinearLayout {
    private ImageView ivCalendarLeft;
    private TextView tvCalendarYear;
    private ImageView ivCalendarRight;
    private Button btnCalendarMonth1;
    private Button btnCalendarMonth2;
    private Button btnCalendarMonth3;
    private Button btnCalendarMonth4;
    private Button btnCalendarMonth5;
    private Button btnCalendarMonth6;
    private Button btnCalendarMonth7;
    private Button btnCalendarMonth8;
    private Button btnCalendarMonth9;
    private Button btnCalendarMonth10;
    private Button btnCalendarMonth11;
    private Button btnCalendarMonth12;

    private Context mContext;
    private Calendar calendar;

    public interface OnCalendarSelectListener {
        void onCalendarSelect(Calendar calendar);
    }

    private OnCalendarSelectListener onCalendarSelectListener;

    public OnCalendarSelectListener getOnCalendarSelectListener() {
        return onCalendarSelectListener;
    }

    public void setOnCalendarSelectListener(OnCalendarSelectListener onCalendarSelectListener) {
        this.onCalendarSelectListener = onCalendarSelectListener;
    }

    public MonthCalendarView(Context context) {
        super(context);
        init(context);
    }

    public MonthCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MonthCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    private void init(Context context) {
        mContext = context;
        calendar = Calendar.getInstance();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return;
        inflater.inflate(R.layout.view_calendar, this, true);

        ivCalendarLeft = (ImageView) findViewById(R.id.iv_calendar_left);
        ivCalendarRight = (ImageView) findViewById(R.id.iv_calendar_right);
        tvCalendarYear = (TextView) findViewById(R.id.tv_calendar_year);

        btnCalendarMonth1 = (Button) findViewById(R.id.btn_calendar_month_1);
        btnCalendarMonth2 = (Button) findViewById(R.id.btn_calendar_month_2);
        btnCalendarMonth3 = (Button) findViewById(R.id.btn_calendar_month_3);
        btnCalendarMonth4 = (Button) findViewById(R.id.btn_calendar_month_4);
        btnCalendarMonth5 = (Button) findViewById(R.id.btn_calendar_month_5);
        btnCalendarMonth6 = (Button) findViewById(R.id.btn_calendar_month_6);
        btnCalendarMonth7 = (Button) findViewById(R.id.btn_calendar_month_7);
        btnCalendarMonth8 = (Button) findViewById(R.id.btn_calendar_month_8);
        btnCalendarMonth9 = (Button) findViewById(R.id.btn_calendar_month_9);
        btnCalendarMonth10 = (Button) findViewById(R.id.btn_calendar_month_10);
        btnCalendarMonth11 = (Button) findViewById(R.id.btn_calendar_month_11);
        btnCalendarMonth12 = (Button) findViewById(R.id.btn_calendar_month_12);

        setCurrentDate(calendar);
        initListeners();
    }

    public void setCurrentDate(Calendar calendar) {
        tvCalendarYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                updateCheckedUI(btnCalendarMonth1);
                break;
            case Calendar.FEBRUARY:
                updateCheckedUI(btnCalendarMonth2);
                break;
            case Calendar.MARCH:
                updateCheckedUI(btnCalendarMonth3);
                break;
            case Calendar.APRIL:
                updateCheckedUI(btnCalendarMonth4);
                break;
            case Calendar.MAY:
                updateCheckedUI(btnCalendarMonth5);
                break;
            case Calendar.JUNE:
                updateCheckedUI(btnCalendarMonth6);
                break;
            case Calendar.JULY:
                updateCheckedUI(btnCalendarMonth7);
                break;
            case Calendar.AUGUST:
                updateCheckedUI(btnCalendarMonth8);
                break;
            case Calendar.SEPTEMBER:
                updateCheckedUI(btnCalendarMonth9);
                break;
            case Calendar.OCTOBER:
                updateCheckedUI(btnCalendarMonth10);
                break;
            case Calendar.NOVEMBER:
                updateCheckedUI(btnCalendarMonth11);
                break;
            case Calendar.DECEMBER:
                updateCheckedUI(btnCalendarMonth12);
                break;
            default:
                throw new RuntimeException("Month must be 1 ~ 12");
        }
    }

    private void updateCheckedUI(Button button) {
        btnCalendarMonth1.setSelected(false);
        btnCalendarMonth2.setSelected(false);
        btnCalendarMonth3.setSelected(false);
        btnCalendarMonth4.setSelected(false);
        btnCalendarMonth5.setSelected(false);
        btnCalendarMonth6.setSelected(false);
        btnCalendarMonth7.setSelected(false);
        btnCalendarMonth8.setSelected(false);
        btnCalendarMonth9.setSelected(false);
        btnCalendarMonth10.setSelected(false);
        btnCalendarMonth11.setSelected(false);
        btnCalendarMonth12.setSelected(false);

        btnCalendarMonth1.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth2.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth3.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth4.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth5.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth6.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth7.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth8.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth9.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth10.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth11.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        btnCalendarMonth12.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));

        button.setSelected(true);
        button.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray2));
    }

    private void initListeners() {
        ivCalendarLeft.setOnClickListener(v -> {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            tvCalendarYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            if (onCalendarSelectListener != null)
                onCalendarSelectListener.onCalendarSelect(calendar);
        });
        ivCalendarRight.setOnClickListener(v -> {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
            tvCalendarYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            if (onCalendarSelectListener != null)
                onCalendarSelectListener.onCalendarSelect(calendar);
        });

        btnCalendarMonth1.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth2.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth3.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth4.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth5.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth6.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth7.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth8.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth9.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth10.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth11.setOnClickListener(this::attemptMonthClick);
        btnCalendarMonth12.setOnClickListener(this::attemptMonthClick);
    }

    private void attemptMonthClick(View view) {
        if (!(view instanceof Button)) return;//must be button

        Button button = (Button) view;
        updateCheckedUI(button);

        switch (view.getId()) {
            case R.id.btn_calendar_month_1:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case R.id.btn_calendar_month_2:
                calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                break;
            case R.id.btn_calendar_month_3:
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case R.id.btn_calendar_month_4:
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case R.id.btn_calendar_month_5:
                calendar.set(Calendar.MONTH, Calendar.MAY);
                break;
            case R.id.btn_calendar_month_6:
                calendar.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case R.id.btn_calendar_month_7:
                calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case R.id.btn_calendar_month_8:
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                break;
            case R.id.btn_calendar_month_9:
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case R.id.btn_calendar_month_10:
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            case R.id.btn_calendar_month_11:
                calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                break;
            case R.id.btn_calendar_month_12:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                break;
            default:
                throw new RuntimeException("Month must be 1~12");
        }

        if (onCalendarSelectListener != null)
            onCalendarSelectListener.onCalendarSelect(calendar);
    }
}
