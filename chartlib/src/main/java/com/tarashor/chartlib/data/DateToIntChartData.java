package com.tarashor.chartlib.data;


import java.io.Serializable;
import java.util.Date;

public final class DateToIntChartData extends ChartData<Date, Integer> implements Serializable {
    public DateToIntChartData(Date[] xValues, Line<Integer>[] lines) {
        super(xValues, lines);
    }
}
