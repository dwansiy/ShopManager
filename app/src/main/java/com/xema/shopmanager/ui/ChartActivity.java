package com.xema.shopmanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.ChartAdapter;
import com.xema.shopmanager.model.Chart;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.widget.MonthCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xema0 on 2018-03-09.
 */

public class ChartActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.iv_chart)
    ImageView ivChart;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.mcv_main)
    MonthCalendarView mcvMain;
    @BindView(R.id.tv_current_year)
    TextView tvCurrentYear;
    @BindView(R.id.tv_year_total_price)
    TextView tvYearTotalPrice;
    @BindView(R.id.tv_current_month)
    TextView tvCurrentMonth;
    @BindView(R.id.tv_month_total_price)
    TextView tvMonthTotalPrice;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;

    private List<Chart> mList;
    private ChartAdapter mAdapter;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        Calendar calendar = mcvMain.getCalendar();

        initToolbar(calendar);
        initListeners();
        initAdapter();

        queryPrice(calendar);
    }

    private void initToolbar(Calendar calendar) {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvTitle.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvSubTitle.setText(getString(R.string.format_month, calendar.get(Calendar.MONTH)));
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        llTitle.setOnClickListener(this::showCalendar);
        ivChart.setOnClickListener(this::showChart);

        mcvMain.setOnCalendarSelectListener(calendar -> {
            Toast.makeText(this, "" + calendar.get(Calendar.YEAR), Toast.LENGTH_SHORT).show();
            updateDateUI(calendar);
            queryChart(calendar);
        });
    }

    private void initAdapter() {
        mList = new ArrayList<>();
        mAdapter = new ChartAdapter(this, mList);
        rvMain.setAdapter(mAdapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.setHasFixedSize(false);

        rvMain.setNestedScrollingEnabled(false);
    }

    private void queryPrice(Calendar calendar) {
        // TODO: 2018-03-10
        updateDateUI(calendar);
    }

    private void queryChart(Calendar cal) {
        if (mList == null) mList = new ArrayList<>();
        else mList.clear();

        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date lastDayOfMonth = cal.getTime();

        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        Date startDayOfMonth = cal.getTime();

        RealmResults<Sales> list = realm.where(Sales.class).greaterThanOrEqualTo("selectedAt", startDayOfMonth).lessThan("selectedAt", lastDayOfMonth).sort("selectedAt", Sort.DESCENDING).findAll();
        // TODO: 2018-03-11 리스트 매핑

        mAdapter.notifyParentDataSetChanged(false);
    }

    private void updateDateUI(Calendar calendar) {
        tvTitle.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvSubTitle.setText(getString(R.string.format_month, calendar.get(Calendar.MONTH) + 1));
        //tvSubTitle.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));

        tvCurrentYear.setText(getString(R.string.format_year, calendar.get(Calendar.YEAR)));
        tvCurrentMonth.setText(getString(R.string.format_month, calendar.get(Calendar.MONTH) + 1));
        //tvCurrentMonth.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        // TODO: 2018-03-11
        tvYearTotalPrice.setText(CommonUtil.toDecimalFormat(13002000));
        tvMonthTotalPrice.setText(CommonUtil.toDecimalFormat(90000));
    }

    private void showCalendar(View view) {
        if (mcvMain.getVisibility() == View.GONE) {
            mcvMain.setVisibility(View.VISIBLE);
        } else {
            mcvMain.setVisibility(View.GONE);
        }
    }

    private void showChart(View view) {
        // TODO: 2018-03-09
    }

}
