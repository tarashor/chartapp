package com.tarashor.chartlib.data;
import android.graphics.Color;

import java.util.Arrays;

import androidx.core.content.res.TypedArrayUtils;

abstract class ChartData<XType extends Comparable<XType>, YType extends Comparable<YType>> {
    private final XType[] xValues;
    private final Line<YType>[] lines;

    public ChartData(XType[] xValues, Line<YType>[] lines) {
        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.lines = (Line<YType>[]) getEnableLines(lines);
//        for (int i = 0; i < lines.length; i++){
//            this.lines[i] = lines[i].copy();
//        }
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

    public Line[] getEnableLines(Line[] lines){
        Line[] res = new Line[getEnabledLinesCount(lines)];
        int curIndex = 0;
        for(int i = 0; i< lines.length; i++){
            if(lines[i].isEnabled()){
                res[curIndex] = lines[i];
                curIndex++;
            }
        }

        return res;
    }

    public int getEnabledLinesCount(Line[] lines){
        int count = lines.length;
        for(int i = 0; i < lines.length; i++){
            if(!lines[i].isEnabled()){
                count--;
            }
        }

        return count;
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


    public int getColor(int index) {
        if (index >= 0 && index < getLinesCount()) {
            return Color.parseColor(lines[index].getColor());
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }


}
