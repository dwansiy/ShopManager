package com.xema.shopmanager.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Sales;
import com.xema.shopmanager.utils.CommonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xema0 on 2018-07-05.
 */

public class ChartGraphFragment extends Fragment {
    @BindView(R.id.tv_total_cash)
    TextView tvTotalCash;
    @BindView(R.id.tv_total_card)
    TextView tvTotalCard;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.bc_month_price)
    BarChart bcMonthPrice;
    @BindView(R.id.bc_month_count)
    BarChart bcMonthCount;
    @BindColor(R.color.colorGray5)
    int colorLightGray;
    @BindColor(R.color.colorYellow)
    int colorYellow;
    @BindColor(R.color.colorRedDark)
    int colorRed;

    Unbinder unbinder;

    private int currentYear;
    private Context mContext;

    private IAxisValueFormatter mMonthValueFormatter = new MonthValueFormatter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_graph, null);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            Bundle b = getArguments();
            currentYear = b.getInt("year");
            if (currentYear != 0) {
                initHeaderUI();
                initPriceChart();
                new ChartAsyncTask(this).execute(getStartCalendar(currentYear), getLastCalendar(currentYear));
            }
        }

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initPriceChart() {
        bcMonthPrice.setNoDataText(getString(R.string.message_chart_no_data_price));
        bcMonthPrice.getAxisLeft().setEnabled(true);
        bcMonthPrice.getAxisLeft().setAxisLineColor(colorLightGray);
        bcMonthPrice.getAxisLeft().setTextColor(colorLightGray);
        bcMonthPrice.setMaxVisibleValueCount(12);
        bcMonthPrice.getAxisRight().setEnabled(false);
        bcMonthPrice.getLegend().setEnabled(false);
        bcMonthPrice.getDescription().setEnabled(false);
        bcMonthPrice.setDoubleTapToZoomEnabled(false);
        bcMonthPrice.setPinchZoom(false);
        bcMonthPrice.setHighlightPerDragEnabled(false);
        bcMonthPrice.setHighlightPerTapEnabled(false);
        bcMonthPrice.getAxisLeft().setGranularity(1f);
        XAxis xAxisPriceChart = bcMonthPrice.getXAxis();
        xAxisPriceChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisPriceChart.setEnabled(true);
        xAxisPriceChart.setDrawAxisLine(false);
        xAxisPriceChart.setDrawGridLines(false);
        xAxisPriceChart.setDrawLabels(true);
        xAxisPriceChart.setGranularity(1f);
        xAxisPriceChart.setLabelCount(12);
        xAxisPriceChart.setValueFormatter(mMonthValueFormatter);
        xAxisPriceChart.setTextColor(colorLightGray);

        bcMonthCount.setNoDataText(getString(R.string.message_chart_no_data_count));
        bcMonthCount.getAxisLeft().setEnabled(true);
        bcMonthCount.getAxisLeft().setAxisLineColor(colorLightGray);
        bcMonthCount.getAxisLeft().setTextColor(colorLightGray);
        bcMonthCount.setMaxVisibleValueCount(12);
        bcMonthCount.getAxisRight().setEnabled(false);
        bcMonthCount.getLegend().setEnabled(false);
        bcMonthCount.getDescription().setEnabled(false);
        bcMonthCount.setDoubleTapToZoomEnabled(false);
        bcMonthCount.setPinchZoom(false);
        bcMonthCount.setHighlightPerDragEnabled(false);
        bcMonthCount.setHighlightPerTapEnabled(false);
        bcMonthCount.getAxisLeft().setGranularity(1f);
        XAxis xAxisCountChart = bcMonthCount.getXAxis();
        xAxisCountChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisCountChart.setEnabled(true);
        xAxisCountChart.setDrawAxisLine(false);
        xAxisCountChart.setDrawGridLines(false);
        xAxisCountChart.setDrawLabels(true);
        xAxisCountChart.setGranularity(1f);
        xAxisCountChart.setLabelCount(12);
        xAxisCountChart.setValueFormatter(mMonthValueFormatter);
        xAxisCountChart.setTextColor(colorLightGray);
    }

    private Calendar getStartCalendar(int year) {
        Calendar cStart = Calendar.getInstance();
        cStart.set(Calendar.YEAR, year);
        cStart.set(Calendar.MONTH, cStart.getActualMinimum(Calendar.MONTH));
        cStart.set(Calendar.DAY_OF_MONTH, cStart.getActualMinimum(Calendar.DAY_OF_MONTH));
        cStart.set(Calendar.HOUR_OF_DAY, cStart.getActualMinimum(Calendar.HOUR_OF_DAY));
        cStart.set(Calendar.MINUTE, cStart.getActualMinimum(Calendar.MINUTE));
        cStart.set(Calendar.SECOND, cStart.getActualMinimum(Calendar.SECOND));
        return cStart;
    }

    private Calendar getLastCalendar(int year) {
        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.YEAR, year);
        cEnd.set(Calendar.MONTH, cEnd.getActualMaximum(Calendar.MONTH));
        cEnd.set(Calendar.DAY_OF_MONTH, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        cEnd.set(Calendar.HOUR_OF_DAY, cEnd.getActualMaximum(Calendar.HOUR_OF_DAY));
        cEnd.set(Calendar.MINUTE, cEnd.getActualMaximum(Calendar.MINUTE));
        cEnd.set(Calendar.SECOND, cEnd.getActualMaximum(Calendar.SECOND));
        return cEnd;
    }

    private boolean isEmptyChart(List<BarEntry> entryList) {
        long yVal = 0;
        if (!entryList.isEmpty()) {
            for (BarEntry barEntry : entryList) {
                yVal += (long) barEntry.getY();
            }
        }
        return yVal == 0;
    }

    private void visualizeChart(Map<String, List<BarEntry>> map) {
        List<BarEntry> priceEntryList = map.get("price");
        if (!isEmptyChart(priceEntryList)) {
            BarDataSet priceSet = new BarDataSet(priceEntryList, getString(R.string.title_graph_month_sales_price));
            //priceSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> getString(R.string.chart_format_price, (int) value));
            BarData priceData = new BarData(priceSet);
            priceSet.setColors(colorYellow);
            priceData.setValueTextColor(colorYellow);
            priceData.setBarWidth(0.9f);
            bcMonthPrice.setData(priceData);
            bcMonthPrice.setFitBars(true);
            bcMonthPrice.animateY(1000, Easing.EasingOption.EaseOutSine);
        }

        List<BarEntry> countEntryList = map.get("count");
        if (!isEmptyChart(countEntryList)) {
            BarDataSet countSet = new BarDataSet(countEntryList, getString(R.string.title_graph_month_sales_count));
            countSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> getString(R.string.chart_format_count, (int) value));
            BarData countData = new BarData(countSet);
            countSet.setColors(colorRed);
            countData.setValueTextColor(colorRed);
            countData.setBarWidth(0.9f);
            bcMonthCount.setData(countData);
            bcMonthCount.setFitBars(true);
            bcMonthCount.animateY(1000, Easing.EasingOption.EaseOutSine);
        }
    }

    private void initHeaderUI() {
        tvTotalCash.setText(getString(R.string.common_loading));
        tvTotalCard.setText(getString(R.string.common_loading));
        tvTotalPrice.setText(getString(R.string.common_loading));
    }

    private void updateHeaderUI(List<Sales> salesList) {
        long cash = 0;
        long card = 0;
        long price = 0;
        for (Sales sales : salesList) {
            long tmp = sales.getPrice();
            if (sales.getType() == Sales.Type.CASH) {
                cash += tmp;
            } else if (sales.getType() == Sales.Type.CARD) {
                card += tmp;
            }
            price += tmp;
        }

        tvTotalCash.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(cash)));
        tvTotalCard.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(card)));
        tvTotalPrice.setText(getString(R.string.format_price, CommonUtil.toDecimalFormat(price)));
    }

    @WorkerThread
    private void sendListToUiThread(List<Sales> sales) {
        new Handler(Looper.getMainLooper()).post(() -> updateHeaderUI(sales));
    }

    private class MonthValueFormatter implements IAxisValueFormatter {
        String[] mMonths = new String[]{
                //"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
                "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
        };

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String s = "";
            try {
                s = mMonths[((int) value)];
            } catch (ArrayIndexOutOfBoundsException e) {
                //do nothing
            }
            return s;
        }
    }

    private static final class ChartAsyncTask extends AsyncTask<Calendar, Void, Map<String, List<BarEntry>>> {
        private final WeakReference<ChartGraphFragment> ref;

        ChartAsyncTask(ChartGraphFragment ref) {
            this.ref = new WeakReference<>(ref);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // TODO: 2018-07-08 로딩화면 표시
        }

        @Override
        protected void onPostExecute(Map<String, List<BarEntry>> map) {
            super.onPostExecute(map);
            ChartGraphFragment fragment = ref.get();
            if (fragment == null) return;
            fragment.visualizeChart(map);
        }

        @Override
        protected Map<String, List<BarEntry>> doInBackground(Calendar... calendars) {
            Calendar cStart = calendars[0];
            Calendar cEnd = calendars[1];

            try (Realm realm = Realm.getDefaultInstance()) {
                Map<String, List<BarEntry>> listMap = new HashMap<>();

                RealmResults<Sales> list = realm.where(Sales.class).greaterThanOrEqualTo("selectedAt", cStart.getTime()).lessThan("selectedAt", cEnd.getTime()).sort("selectedAt", Sort.ASCENDING).findAll();
                ChartGraphFragment fragment = ref.get();
                if (fragment != null) {
                    fragment.sendListToUiThread(realm.copyFromRealm(list));
                }

                List<BarEntry> priceEntryList = new ArrayList<>();
                List<BarEntry> countEntryList = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    priceEntryList.add(new BarEntry(i, 0));
                    countEntryList.add(new BarEntry(i, 0));
                }
                while (cStart.before(cEnd)) {
                    for (Sales sales : list) {
                        if (sales.getSelectedAt().getMonth() == cStart.getTime().getMonth()) {
                            float y1 = priceEntryList.get(cStart.getTime().getMonth()).getY();
                            priceEntryList.get(cStart.getTime().getMonth()).setY(y1 + sales.getPrice());

                            // TODO: 2018-07-15 기준이 명확하지 않음....기획보고 다시 판단
                            //purchase 의 count 총합 개수기준일떄
                            float y2 = countEntryList.get(cStart.getTime().getMonth()).getY();
                            countEntryList.get(cStart.getTime().getMonth()).setY(y2 + sales.getTotalPurchaseCount());

                            //sales 기준일때
                            //float y2 = countEntryList.get(cStart.getTime().getMonth()).getY();
                            //countEntryList.get(cStart.getTime().getMonth()).setY(y2 + 1);
                        }
                    }
                    cStart.add(Calendar.MONTH, 1);
                }

                listMap.put("price", priceEntryList);
                listMap.put("count", countEntryList);
                return listMap;
            }
        }
    }
}
