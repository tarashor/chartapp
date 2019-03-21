package com.tarashor.chartlib.chart;

import com.tarashor.chartlib.IValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateValueFormatter implements IValueFormatter<Date> {
    @Override
    public String format(Date v) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.ENGLISH);
        return sdf.format(v);
    }

}

