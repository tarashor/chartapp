package com.tarashor.chartlib.chart;

import com.tarashor.chartlib.IValueConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class IntegerValueConverter implements IValueConverter<Integer> {
    private final Integer mMin;
    private final Integer mMax;
    private final float scaleValue;
    private float mChartSizePixels;


    public IntegerValueConverter(Integer min, Integer max, float chartSizePixels){
        mMin = min;
        mMax = max;
        mChartSizePixels = chartSizePixels;
        scaleValue = (convertDateToFloat(mMax) - convertDateToFloat(mMin)) / mChartSizePixels;
    }

    @Override
    public String format(Integer v) {
        return String.valueOf(v);
    }

    @Override
    public float valueToPixels(Integer v) {
        return mChartSizePixels - (convertDateToFloat(v) - convertDateToFloat(mMin)) / scaleValue;
    }

    @Override
    public Integer pixelsToValue(float pixels) {
        return mMin + Math.round((mChartSizePixels - pixels) * scaleValue);
    }

    private float convertDateToFloat(Integer v){
        return v;
    }

}

