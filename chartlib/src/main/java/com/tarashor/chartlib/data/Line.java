package com.tarashor.chartlib.data;

import java.util.Arrays;

public final class Line<YType extends Comparable<YType>> {
    private final String name;
    private final YType[] yValues;
    private final String color;
    private final YType min;
    private final YType max;
    private boolean enabled;


    public Line(String name, YType[] yValues, String color, boolean enabled) {
        this.name = name;
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
        this.enabled = enabled;
    }

    public Line<YType> copy() {
        return new Line<>(name, yValues, color, enabled);
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

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
