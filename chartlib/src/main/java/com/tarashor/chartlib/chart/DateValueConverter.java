package com.tarashor.chartlib.chart;

import com.tarashor.chartlib.IValueConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateValueConverter implements IValueConverter<Date> {
    private final Date mMin;
    private final Date mMax;
    private final float scaleValue;


    public DateValueConverter(Date min, Date max, float chartSizePixels){
        mMin = min;
        mMax = max;
        scaleValue = (convertDateToFloat(mMax) - convertDateToFloat(mMin)) / chartSizePixels;
    }

    @Override
    public String format(Date v) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.ENGLISH);
        return sdf.format(v);
    }

    @Override
    public float valueToPixels(Date v) {
        return (convertDateToFloat(v) - convertDateToFloat(mMin)) / scaleValue;
    }

    @Override
    public Date pixelsToValue(float pixels) {
        Calendar c = Calendar.getInstance();
        c.setTime(mMin);
        c.add(Calendar.MILLISECOND, Math.round(pixels * scaleValue));
        return c.getTime();
    }

    private float convertDateToFloat(Date date){
        return date.getTime();
    }

}

