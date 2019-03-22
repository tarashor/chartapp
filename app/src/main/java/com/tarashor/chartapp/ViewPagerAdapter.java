package com.tarashor.chartapp;

import android.os.Parcelable;
import android.view.View;

import com.tarashor.chartapp.models.TelegramFileData;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<ChartFragment> chartFragments = new ArrayList<>();

    public ViewPagerAdapter(List<ChartFragment> chartFragments, FragmentManager fragmentManager){
        super(fragmentManager);
        this.chartFragments.addAll(chartFragments);
    }

    @Override
    public int getCount() {
        return chartFragments.size();
    }

    @Override
    public ChartFragment getItem(int position) {
        return chartFragments.get(position);
    }
}
