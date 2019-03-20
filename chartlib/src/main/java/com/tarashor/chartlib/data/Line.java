package com.tarashor.chartlib.data;

import java.util.Arrays;

public final class Line<YType extends Comparable<YType>> {
    private final YType[] yValues;
    private final String color;
    private final YType min;
    private final YType max;

    public Line(YType[] yValues, String color) {
        this.yValues = Arrays.copyOf(yValues, yValues.length);
        this.color = color;

        YType min = yValues[0];
        YType max = yValues[0];
        for (int i = 1; i < yValues.length; i++) {
            YType y = yValues[i];
            if (y.compareTo(max) > 0){
                max = y;
            }
            if (y.compareTo(min) < 0){
                min = y;
            }
        }

        this.min = min;
        this.max = max;
    }

    public Line<YType> copy() {
        return new Line<>(yValues, color);
    }

    public YType getMin() {
        return min;
    }

    public YType getMax() {
        return max;
    }

    public YType getY(int index){
        return yValues[index];
    }


    public String getColor() {
        return color;
    }
}
