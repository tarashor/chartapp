package com.tarashor.chartapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.mikephil.charting.data.ChartData;
import com.tarashor.chartapp.models.Column;
import com.tarashor.chartapp.models.TelegramFileData;
import com.tarashor.chartapp.models.ChartToChartDataConverter;
import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<TelegramFileData> telegramFileData;
    private ViewPager chartViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChartJsonParser parser = new ChartJsonParser();
        telegramFileData = parser.parseColumns(this);
        chartViewPager = findViewById(R.id.viewPager);

        List<ChartFragment> chartFragments = new ArrayList<>();
        for(TelegramFileData telegramFileData : telegramFileData){
            ChartFragment chartFragment = ChartFragment.createInstance(telegramFileData);
            chartFragments.add(chartFragment);
        }

        final ViewPagerAdapter adapter = new ViewPagerAdapter(chartFragments, getSupportFragmentManager());
        chartViewPager.setAdapter(adapter);
    }


    private void setData(int count, int range) {
        // now in hours

//        Date[] dates = new Date[count];
//        Integer[] yValues = new Integer[count];
//
//        for (int i = 0; i < count; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.HOUR, i);
//            dates[i] = calendar.getTime();
//
//            yValues[i] = getRandom(range, 50);
//        }

//
//
//        Line<Integer> line = new Line<>(yValues, "#FF0000");
//
//        DateToIntChartData chartData = new DateToIntChartData(dates, new Line[]{line});
//
//        telegramChart.setData(chartData);


//        if(chartIndex < telegramFileData.size()) {
//            addCheckBoxes(telegramFileData.get(chartIndex));
//            DateToIntChartData chartData = new ChartToChartDataConverter().convert(telegramFileData.get(chartIndex));
//            telegramChart.setData(chartData);
//        }

    }

//    private void addCheckBoxes(final TelegramFileData fileData) {
//        List<CheckBoxItem> items = new ArrayList<>();
//        for(Column<Integer> column : fileData.getYColumns()){
//            CheckBoxItem checkBoxItem = new CheckBoxItem();
//            checkBoxItem.setColor(column.getColor());
//            checkBoxItem.setName(column.getName());
//            checkBoxItem.setEnabled(column.isEnabled());
//            items.add(checkBoxItem);
//        }
//
//        if(checkBoxAdapter == null) {
//            checkBoxAdapter = new CheckBoxAdapter(this, items);
//            checkBoxList.setAdapter(checkBoxAdapter);
//            checkBoxAdapter.setOnCheckedChangeListener(new CheckBoxAdapter.CheckedChange() {
//                @Override
//                public void onChange(boolean checked, int pos) {
//                    fileData.getYColumns().get(pos).setEnabled(checked);
//                    setData(0, 0);
//                }
//            });
//        } else {
//            checkBoxAdapter.setCheckBoxItems(items);
//        }
//    }
}
