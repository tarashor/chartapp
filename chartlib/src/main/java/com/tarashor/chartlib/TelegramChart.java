package com.tarashor.chartlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
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
        //initAttrs(null, 0, 0);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(attrs, 0, 0);
    }

    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TelegramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        initAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TelegramChart, defStyleAttr, defStyleRes);

        int gridColor = a.getColor(R.styleable.TelegramChart_gridColor, Color.GRAY);
        int marksTextColor = a.getColor(R.styleable.TelegramChart_marksTextColor, Color.GRAY);
        int pointerLineColor = a.getColor(R.styleable.TelegramChart_pointerLineColor, Color.GRAY);
        int pointerPopupBackground = a.getColor(R.styleable.TelegramChart_pointerPopupBackground, Color.WHITE);
        int pointerPopupBorderColor = a.getColor(R.styleable.TelegramChart_pointerPopupBorderColor, Color.GRAY);
        int pointerTextHeaderColor = a.getColor(R.styleable.TelegramChart_pointerTextHeaderColor, Color.BLACK);

        a.recycle();

        chart.setColorsForPaints(gridColor,
                marksTextColor,
                pointerLineColor,
                pointerTextHeaderColor,
                pointerPopupBackground,
                pointerPopupBorderColor);

    }


    private void init(Context context) {
        inflate(context, R.layout.telegram_chart_layout,this);
        chart = findViewById(R.id.telegram_chart_view);
        rangeSelector = findViewById(R.id.telegram_range_view);
        chart.setId(this.getId());
        rangeSelector.setId(this.getId());
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
