package com.xema.shopmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.xema.shopmanager.R;
import com.xema.shopmanager.widget.NonSwipeableViewPager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xema0 on 2018-03-09.
 */

// TODO: 2018-07-15 금액 합산 리사이클러뷰 헤더로 바꾸기 -> 포커스떄문에 금액 합산한게 안보임...
public class ChartGraphActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_show_list)
    ImageView ivShowList;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tab_year)
    TabLayout tabYear;
    @BindView(R.id.vp_main)
    NonSwipeableViewPager vpMain;

    private static final int YEAR_START = 2015;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_graph);
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        initViewPager();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        ivShowList.setOnClickListener(this::redirectListActivity);
    }

    private void initViewPager() {
        //2012 년부터 초기화
        Calendar calendar = Calendar.getInstance();
        int end = calendar.get(Calendar.YEAR);
        int[] years = new int[end - YEAR_START + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = end - i;
        }

        vpMain.setOffscreenPageLimit(1);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), years);
        vpMain.setAdapter(sectionsPagerAdapter);
        tabYear.setupWithViewPager(vpMain);
    }

    private void redirectListActivity(View view) {
        Intent intent = new Intent(this, ChartListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, CustomerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.finish();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int[] years;
        private ChartGraphFragment[] fragments;

        SectionsPagerAdapter(FragmentManager fm, int[] years) {
            super(fm);
            this.years = years;
            this.fragments = new ChartGraphFragment[years.length];
            for (int i = 0; i < fragments.length; i++) {
                fragments[i] = new ChartGraphFragment();
                Bundle b = new Bundle();
                b.putInt("year", years[i]);
                fragments[i].setArguments(b);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(years[position]);
        }
    }
}
