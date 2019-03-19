package com.tarashor.chartapp.models;

import com.tarashor.chartlib.ChartData;
import com.tarashor.chartlib.DateToIntChartData;
import com.tarashor.chartlib.Line;

import java.util.Calendar;
import java.util.Date;

public class ChartToChartDataConverter {
    public DateToIntChartData convert(Chart chart){
        Date[] x = new Date[chart.getX().getColumnsData().size()];
        for (int i = 0; i < chart.getX().getColumnsData().size(); i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chart.getX().getColumnsData().get(i));
            x[i] = calendar.getTime();
        }




        Line<Integer>[] lines = new Line[chart.getColumns().size() - 1];
        for(int i = 1; i < chart.getColumns().size(); i++){
            lines[i - 1] = new Line<>(chart.getY(i).getColumnsData().toArray(new Integer[0]), chart.getY(i).getColor());
        }

        return new DateToIntChartData(x, lines);
    }
}
