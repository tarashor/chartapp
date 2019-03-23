package com.tarashor.chartapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tarashor.chartapp.R;
import com.tarashor.chartlib.TelegramChart;
import com.tarashor.chartlib.data.DateToIntChartData;

import androidx.annotation.Nullable;

public class ChartView extends LinearLayout {
    private TelegramChart chart;
    private LinesListView linesListView;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.chart_view, this, true);
        chart = findViewById(R.id.chart);
        linesListView = findViewById(R.id.checkbox_list);

        linesListView.setOnCheckedChangeListener(new LinesListView.CheckedChange() {
            @Override
            public void onChange(boolean checked, String name) {
                chart.setVisibilityForLine(name, checked);
            }
        });
    }

    public void setData(DateToIntChartData chartData){
        chart.setData(chartData);
        linesListView.setData(chartData);
    }
}
