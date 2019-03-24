package com.tarashor.chartlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tarashor.chartlib.chart.Chart;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Date;


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
        //chart.setId(View.generateViewId());
        //rangeSelector.setId(View.generateViewId());
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


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.chartData = this.mData;
        ss.vxmin = chart.getXRange().first;//viewPort.getXmin();
        ss.vxmax = chart.getXRange().second;
        //ss.dataLines = new ArrayList<>();
        ss.dataLines = chart.dataLines;
        ss.xmin = chart.xmin;
        ss.xmax = chart.xmax;
        ss.start = rangeSelector.start;
        ss.end = rangeSelector.end;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }


        SavedState ss = (SavedState)state;
        this.mData = ss.chartData;
        chart.restore(mData, ss.xmin, ss.xmax, ss.dataLines, ss.vxmin, ss.vxmax);
        rangeSelector.start = ss.start;
        rangeSelector.end = ss.end;
        rangeSelector.restore(mData, ss.xmin, ss.xmax, ss.dataLines, null, null);

        super.onRestoreInstanceState(ss.getSuperState());
    }



    static class SavedState extends BaseSavedState {
        DateToIntChartData chartData;
        Date xmin;
        Date xmax;
        BaseChartView.DateToIntDataLine[] dataLines;
        Date vxmin;
        Date vxmax;
        Date start;
        Date end;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.chartData = (DateToIntChartData) in.readBundle().getSerializable("chart_data");
            this.xmin = (Date) in.readBundle().getSerializable("chart_xmin");
            this.xmax = (Date) in.readBundle().getSerializable("chart_xmax");
            this.dataLines = (BaseChartView.DateToIntDataLine[]) in.readBundle().getSerializable("line_data");
            this.vxmin = (Date) in.readBundle().getSerializable("line_vxmin");
            this.vxmax = (Date) in.readBundle().getSerializable("line_vxmax");
            this.start = (Date) in.readBundle().getSerializable("line_start");
            this.end = (Date) in.readBundle().getSerializable("line_end");
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            Bundle bundle = new Bundle();
            bundle.putSerializable("chart_data", chartData);
            bundle.putSerializable("chart_xmin", xmin);
            bundle.putSerializable("chart_xmax", xmax);
            bundle.putSerializable("line_data", dataLines);
            bundle.putSerializable("line_vxmin", vxmin);
            bundle.putSerializable("line_vxmax", vxmax);
            bundle.putSerializable("line_start", start);
            bundle.putSerializable("line_end", end);
            out.writeBundle(bundle);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


}
