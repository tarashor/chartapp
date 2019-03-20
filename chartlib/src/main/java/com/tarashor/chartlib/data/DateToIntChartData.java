package com.tarashor.chartlib.data;


import java.util.Date;

public final class DateToIntChartData extends ChartData<Date, Integer> {
    public DateToIntChartData(Date[] xValues, Line<Integer>[] lines) {
        super(xValues, lines);
    }
}
