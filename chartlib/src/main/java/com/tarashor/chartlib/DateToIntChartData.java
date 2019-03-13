package com.tarashor.chartlib;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateToIntChartData extends ChartData<Date, Integer> {

    public DateToIntChartData(Date[] xValues, Line<Integer>[] lines) {
        super(xValues, lines);
    }

    @Override
    public float convertXtoFloat(Date x) {
        return TimeUnit.MILLISECONDS.toHours(x.getTime());
    }

    @Override
    public float convertYtoFloat(Integer y) {
        return y;
    }
}
