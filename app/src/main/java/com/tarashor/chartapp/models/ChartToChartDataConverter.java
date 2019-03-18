package com.tarashor.chartapp.models;

import com.tarashor.chartlib.ChartData;
import com.tarashor.chartlib.Line;

public class ChartToChartDataConverter {
    public ChartData<Integer, Integer> convert(Chart chart){
        Integer[] xArray = new Integer[chart.getX().getColumnsData().size()];
        Integer[] x = chart.getX().getColumnsData().toArray(xArray);
        Line[] lines = new Line[chart.getColumns().size() - 1];

        for(int i = 1; i < chart.getColumns().size(); i++){
            Integer[] yArray = new Integer[chart.getY(i).getColumnsData().size()];
            lines[i - 1] = new Line(chart.getY(i).getColumnsData().toArray(yArray), chart.getY(i).getColor());
        }

        return new ChartData<Integer, Integer>(x, lines) {
            @Override
            protected float convertXtoFloat(Integer x) {
                return (float) x;
            }

            @Override
            protected float convertYtoFloat(Integer y) {
                return (float) y;
            }
        };
    }
}
