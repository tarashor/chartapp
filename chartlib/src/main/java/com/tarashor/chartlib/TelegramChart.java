package com.tarashor.chartlib;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class TelegramChart extends LinearLayout {
    private DateToIntChartData mData;
    private Chart chart;
    private ChartRangeSelector rangeSelector;

    public TelegramChart(Context context) {
        super(context);
        init(context);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, R.layout.telegram_chart_layout,this);
        chart = findViewById(R.id.telegram_chart_view);
        rangeSelector = findViewById(R.id.telegram_range_view);
        rangeSelector.setListener(new ChartRangeSelector.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(ChartRangeSelector v, Date start, Date end) {
                chart.setRange(start, end);
            }
        });
    }

    public void setData(DateToIntChartData data){
        mData = data;
        chart.setData(data);
        rangeSelector.setData(data);
    }

    public void setVisibilityForLine(String lineName, boolean isVisible){
        chart.setVisibilityForLine(lineName, isVisible);
        rangeSelector.setVisibilityForLine(lineName, isVisible);
    }


}
