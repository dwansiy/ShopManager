package com.xema.shopmanager.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.xema.shopmanager.R;
import com.xema.shopmanager.model.Sales;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.bc_month_price)
    BarChart bcMonthPrice;
    @BindView(R.id.bc_month_count)
    BarChart bcMonthCount;

    Unbinder unbinder;

    private int currentYear;
    private Context mContext;

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
                initChart();
                visualizeChart();
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

    private void initChart() {
        bcMonthPrice.getAxisLeft().setEnabled(true);
        bcMonthPrice.getAxisLeft().setAxisLineColor(ContextCompat.getColor(mContext, R.color.colorGray5));
        bcMonthPrice.getAxisLeft().setTextColor(ContextCompat.getColor(mContext, R.color.colorGray5));
        bcMonthPrice.setMaxVisibleValueCount(12);
        XAxis xAxis = bcMonthPrice.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            String[] mMonths = new String[]{
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
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

        });
        xAxis.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray5));


        bcMonthPrice.getAxisRight().setEnabled(false);
        bcMonthPrice.getLegend().setEnabled(false);
        bcMonthPrice.getDescription().setEnabled(false);
        bcMonthPrice.setDoubleTapToZoomEnabled(false);
        bcMonthPrice.setPinchZoom(false);
        bcMonthPrice.setHighlightPerDragEnabled(false);
        bcMonthPrice.setHighlightPerTapEnabled(false);
    }

    private void visualizeChart() {
        Calendar cStart = Calendar.getInstance();
        cStart.set(Calendar.YEAR, currentYear);
        cStart.set(Calendar.MONTH, cStart.getActualMinimum(Calendar.MONTH));
        cStart.set(Calendar.DAY_OF_MONTH, cStart.getActualMinimum(Calendar.DAY_OF_MONTH));
        cStart.set(Calendar.HOUR_OF_DAY, cStart.getActualMinimum(Calendar.HOUR_OF_DAY));
        cStart.set(Calendar.MINUTE, cStart.getActualMinimum(Calendar.MINUTE));
        cStart.set(Calendar.SECOND, cStart.getActualMinimum(Calendar.SECOND));
        Date start = cStart.getTime();

        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.YEAR, currentYear);
        cEnd.set(Calendar.MONTH, cStart.getActualMaximum(Calendar.MONTH));
        cEnd.set(Calendar.DAY_OF_MONTH, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        cEnd.set(Calendar.HOUR_OF_DAY, cEnd.getActualMaximum(Calendar.HOUR_OF_DAY));
        cEnd.set(Calendar.MINUTE, cEnd.getActualMaximum(Calendar.MINUTE));
        cEnd.set(Calendar.SECOND, cEnd.getActualMaximum(Calendar.SECOND));
        Date last = cEnd.getTime();
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Sales> list = realm.where(Sales.class).greaterThanOrEqualTo("selectedAt", start).lessThan("selectedAt", last).sort("selectedAt", Sort.ASCENDING).findAll();
            List<BarEntry> entryList = new ArrayList<>();
            //for (int i = 1; i <= 12; i++) {
            //    entryList.add(new BarEntry(i, (i * i / 2) + 10));
            //    entryList.add(new BarEntry(i, (i * i / 2) + 10));
            //    entryList.add(new BarEntry(i, (i * i / 2) + 10));
            //}
            for (int i = 0; i < 12; i++) {
                entryList.add(new BarEntry(i, 0));
            }
            while (cStart.before(cEnd)) {
                for (Sales sales : list) {
                    if (sales.getSelectedAt().getMonth() == cStart.getTime().getMonth()) {
                        float y = entryList.get(cStart.getTime().getMonth()).getY();
                        entryList.get(cStart.getTime().getMonth()).setY(y + sales.getPrice());
                    }
                }
                cStart.add(Calendar.MONTH, 1);
            }

            // TODO: 2018-07-04 컬러 바꾸고 ui 수정 전체적으로
            BarDataSet set = new BarDataSet(entryList, "월별 매출");
            BarData data = new BarData(set);
            set.setColors(ContextCompat.getColor(mContext, R.color.colorYellow));
            data.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorGray5));
            data.setBarWidth(0.9f); // set custom bar width
            bcMonthPrice.setData(data);
            bcMonthPrice.setFitBars(true); // make the x-axis fit exactly all bars
            bcMonthPrice.invalidate(); // refresh
        }
    }
}
