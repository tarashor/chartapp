package com.tarashor.chartlib;
import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class ChartData<XType extends Comparable<XType>, YType extends Comparable<YType>> {
    private final XType[] xValues;
    private final Line<YType>[] lines;
    private final ArrayList<PointF>[] points;

    public ChartData(XType[] xValues, Line<YType>[] lines) {
        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.lines = (Line<YType>[]) new Line[lines.length];
        this.points = (ArrayList<PointF>[]) new ArrayList[lines.length];
        for (int i = 0; i < lines.length; i++){
            this.lines[i] = lines[i].copy();
            points[i]  = new ArrayList<PointF>();
            int j = 0;
            for (YType y : lines[i].yValues()){
                points[i].add(new PointF(convertXtoFloat(this.xValues[j]), convertYtoFloat(y)));
                j++;
            }
        }

    }

    public int getXCount() {
        return xValues.length;
    }

    public int getLinesCount() {
        return lines.length;
    }

    public YType getYMin(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return lines[index].getMin();
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public YType getYMax(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return lines[index].getMax();
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public XType getXMin() {
        XType min = xValues[0];
        for (int i = 1; i < xValues.length; i++) {
            XType x = xValues[i];
            if (x.compareTo(min) < 0){
                min = x;
            }
        }
        return min;
    }

    public XType getXMax() {
        XType max = xValues[0];
        for (int i = 1; i < xValues.length; i++) {
            XType x = xValues[i];
            if (x.compareTo(max) > 0){
                max = x;
            }
        }
        return max;
    }

    public boolean isEmpty() {
        return xValues == null || lines == null || getLinesCount() == 0 || getXCount() == 0;
    }

    public PointF[] getPoints(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return points[index].toArray(new PointF[0]);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }

    }

    public int getColor(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return Color.parseColor(lines[index].getColor());
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    protected abstract float convertXtoFloat(XType x);
    protected abstract float convertYtoFloat(YType y);
}
