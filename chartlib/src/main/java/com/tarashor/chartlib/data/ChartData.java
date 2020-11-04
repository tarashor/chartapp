package com.tarashor.chartlib.data;
import android.graphics.Color;

import java.util.Arrays;


abstract class ChartData<XType extends Comparable<XType>, YType extends Comparable<YType>> {
    private final XType[] xValues;
    private final Line<YType>[] lines;

    public ChartData(XType[] xValues, Line<YType>[] lines) {
        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.lines = (Line<YType>[]) new Line[lines.length];
        for (int i = 0; i < lines.length; i++){
            this.lines[i] = lines[i].copy();
        }
    }

    public XType getX(int index){
        return xValues[index];
    }

    public YType getY(int lineIndex, int index){
        return lines[lineIndex].getY(index);
    }

    public int getXCount() {
        return xValues.length;
    }

    public int getLinesCount() {
        return lines.length;
    }


    public boolean isEmpty() {
        return xValues == null || lines == null || getLinesCount() == 0 || getXCount() == 0;
    }


    public int getColor(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return Color.parseColor(lines[index].getColor());
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public String getLineName(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return lines[index].getName();
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isEnabled(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return lines[index].isEnabled();
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }


}
