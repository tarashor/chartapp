package com.tarashor.chartlib.chart;

import com.tarashor.chartlib.IValueFormatter;

public class IntegerValueFormatter implements IValueFormatter<Integer> {

    @Override
    public String format(Integer v) {
        return String.valueOf(v);
    }



}

