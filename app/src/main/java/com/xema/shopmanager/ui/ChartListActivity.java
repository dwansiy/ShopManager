package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.adapter.ChartAdapter;
import com.xema.shopmanager.model.Chart;
import com.xema.shopmanager.model.Purchase;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.utils.CommonUtil;
import com.xema.shopmanager.widget.MonthCalendarView;

import org.joda.time.DateTimeComparator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xema0 on 2018-03-09.
 */

public class ChartListActivity extends AppCompatActivity {

    @BindView(R.id.mcv_main)
    MonthCalendarView mcvMain;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.iv_show_graph)
    ImageView ivShowGraph;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.abl_main)
    AppBarLayout ablMain;
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
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;

    private List<Chart> mList;
    private ChartAdapter mAdapter;

    //private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_list);
        ButterKnife.bind(this);
        //realm = Realm.getDefaultInstance();
        Calendar calendar = mcvMain.getCalendar();

        initToolbar(calendar);
        initListeners();
        initAdapter();

        updateYearTotalPrice(calendar);
        queryPrice(calendar);
        queryAndUpdateUI(calendar);
    }

    //@Override
    //protected void onDestroy() {
    //    super.onDestroy();
    //    if (realm != null) {
    //        realm.close();
    //        realm = null;
    //    }
    //}

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
        ivShowGraph.setOnClickListener(this::redirectGraphActivity);

        mcvMain.setOnCalendarSelectListener(calendar -> {
            updateYearTotalPrice(calendar);
            updateDateUI(calendar);
            queryAndUpdateUI(calendar);
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

    // TODO: 2018-07-04 버그 있을거같은데... asynctask 돌아가는동안 액티비티 종료되면? try 안에 exception 확인해보기
    private static final class MappingAsyncTask extends AsyncTask<Calendar, Void, Void> {
        private final WeakReference<ChartListActivity> ref;

        MappingAsyncTask(ChartListActivity ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ChartListActivity activity = ref.get();
            if (activity == null) return;
            activity.llEmpty.setVisibility(View.GONE);
            activity.rvMain.setVisibility(View.GONE);
            activity.pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ChartListActivity activity = ref.get();
            if (activity == null) return;
            activity.pbLoading.setVisibility(View.GONE);
            if (activity.mList.isEmpty()) {
                activity.llEmpty.setVisibility(View.VISIBLE);
            } else {
                activity.rvMain.setVisibility(View.VISIBLE);
            }
            activity.mAdapter.notifyParentDataSetChanged(false);
            activity.mAdapter.expandAllParents();
            // TODO: 2018-07-04 스크롤 버그 수정
            activity.nsvMain.scrollTo(0, 0);

            activity.updateMonthTotalPrice();
        }

        @Override
        protected Void doInBackground(Calendar... calendars) {
            ChartListActivity activity = ref.get();
            if (activity == null) return null;

            if (activity.mList == null) activity.mList = new ArrayList<>();
            else activity.mList.clear();

            //Realm bgRealm = Realm.getDefaultInstance();
            Calendar selectedCal = calendars[0];

            Calendar cStart = Calendar.getInstance();
            cStart.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR));
            cStart.set(Calendar.MONTH, selectedCal.get(Calendar.MONTH));
            cStart.set(Calendar.DAY_OF_MONTH, cStart.getActualMinimum(Calendar.DAY_OF_MONTH));
            cStart.set(Calendar.HOUR_OF_DAY, cStart.getActualMinimum(Calendar.HOUR_OF_DAY));
            cStart.set(Calendar.MINUTE, cStart.getActualMinimum(Calendar.MINUTE));
            cStart.set(Calendar.SECOND, cStart.getActualMinimum(Calendar.SECOND));
            Date startDayOfMonth = cStart.getTime();

            Calendar cEnd = Calendar.getInstance();
            cEnd.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR));
            cEnd.set(Calendar.MONTH, selectedCal.get(Calendar.MONTH));
            cEnd.set(Calendar.DAY_OF_MONTH, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
            cEnd.set(Calendar.HOUR_OF_DAY, cEnd.getActualMaximum(Calendar.HOUR_OF_DAY));
            cEnd.set(Calendar.MINUTE, cEnd.getActualMaximum(Calendar.MINUTE));
            cEnd.set(Calendar.SECOND, cEnd.getActualMaximum(Calendar.SECOND));
            Date lastDayOfMonth = cEnd.getTime();

            try (Realm bgRealm = Realm.getDefaultInstance()) {
                RealmResults<Sales> list = bgRealm.where(Sales.class).greaterThanOrEqualTo("selectedAt", startDayOfMonth).lessThan("selectedAt", lastDayOfMonth).sort("selectedAt", Sort.ASCENDING).findAll();

                while (cStart.before(cEnd)) {
                    List<Purchase> purchaseList = new ArrayList<>();
                    for (Sales sales : list) {
                        if (DateTimeComparator.getDateOnlyInstance().compare(sales.getSelectedAt(), cStart.getTime()) == 0) {
                            List<Purchase> purchases = bgRealm.copyFromRealm(sales.getPurchases());
                            purchaseList.addAll(purchases);

                            // product 가 동일할경우 중복 purchase 제거 -> 리팩토링
                            for (int i = 0; i < purchaseList.size(); i++) {
                                for (int j = 0; j < purchaseList.size(); j++) {
                                    if (i != j && purchaseList.get(j).getProduct().getId().equals(purchaseList.get(i).getProduct().getId())) {
                                        purchaseList.get(i).setCount(purchaseList.get(i).getCount() + purchaseList.get(j).getCount());
                                        purchaseList.remove(j);
                                    }
                                }
                            }
                        }
                    }
                    if (!purchaseList.isEmpty()) {
                        Chart chart = new Chart();
                        chart.setDate(cStart.getTime());
                        chart.setPurchases(purchaseList);
                        activity.mList.add(chart);
                    }
                    cStart.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            return null;
        }
    }

    private void queryAndUpdateUI(Calendar selectedCal) {
        new MappingAsyncTask(this).execute(selectedCal);
    }

    private void updateDateUI(Calendar calendar) {
        tvTitle.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvSubTitle.setText(getString(R.string.format_month, calendar.get(Calendar.MONTH) + 1));
        //tvSubTitle.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));

        tvCurrentYear.setText(getString(R.string.format_year, calendar.get(Calendar.YEAR)));
        tvCurrentMonth.setText(getString(R.string.format_month, calendar.get(Calendar.MONTH) + 1));
        //tvCurrentMonth.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    }

    private void updateMonthTotalPrice() {
        long monthTotal = 0;
        for (Chart chart : mList) {
            for (Purchase purchase : chart.getPurchases()) {
                monthTotal += purchase.getCount() * purchase.getProduct().getPrice();
            }
        }
        tvMonthTotalPrice.setText(CommonUtil.toDecimalFormat(monthTotal));
    }

    // TODO: 2018-07-04 리팩토링 필요한가?
    private void updateYearTotalPrice(Calendar selectedCal) {
        Calendar cStart = Calendar.getInstance();
        cStart.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR));
        cStart.set(Calendar.MONTH, cStart.getActualMinimum(Calendar.MONTH));
        cStart.set(Calendar.DAY_OF_MONTH, cStart.getActualMinimum(Calendar.DAY_OF_MONTH));
        cStart.set(Calendar.HOUR_OF_DAY, cStart.getActualMinimum(Calendar.HOUR_OF_DAY));
        cStart.set(Calendar.MINUTE, cStart.getActualMinimum(Calendar.MINUTE));
        cStart.set(Calendar.SECOND, cStart.getActualMinimum(Calendar.SECOND));
        Date start = cStart.getTime();

        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR));
        cEnd.set(Calendar.MONTH, cStart.getActualMaximum(Calendar.MONTH));
        cEnd.set(Calendar.DAY_OF_MONTH, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        cEnd.set(Calendar.HOUR_OF_DAY, cEnd.getActualMaximum(Calendar.HOUR_OF_DAY));
        cEnd.set(Calendar.MINUTE, cEnd.getActualMaximum(Calendar.MINUTE));
        cEnd.set(Calendar.SECOND, cEnd.getActualMaximum(Calendar.SECOND));
        Date last = cEnd.getTime();

        long yearTotal = 0;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Sales> list = realm.where(Sales.class).greaterThanOrEqualTo("selectedAt", start).lessThan("selectedAt", last).sort("selectedAt", Sort.ASCENDING).findAll();
            for (Sales sales : list) {
                RealmList<Purchase> purchases = sales.getPurchases();
                for (Purchase purchase : purchases) {
                    yearTotal += purchase.getCount() * purchase.getProduct().getPrice();
                }
            }
        }
        tvYearTotalPrice.setText(CommonUtil.toDecimalFormat(yearTotal));
    }

    private void showCalendar(View view) {
        boolean isExpanded = ablMain.getHeight() - ablMain.getBottom() < mcvMain.getHeight() / 2;
        ablMain.setExpanded(!isExpanded);

        //if (mcvMain.getVisibility() == View.GONE) {
        //    mcvMain.setVisibility(View.VISIBLE);
        //} else {
        //    mcvMain.setVisibility(View.GONE);
        //}
    }

    private void redirectGraphActivity(View view) {
        Intent intent = new Intent(this, ChartGraphActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0,0);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, CustomerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.finish();
    }
}
