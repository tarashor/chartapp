package com.tarashor.chartlib;

import android.graphics.PointF;

import java.util.Calendar;
import java.util.Date;

public class ChartViewPort {
    private final Date xmin;
    private final Date xmax;
    private final int ymin;
    private final int ymax;

    private final float width;
    private final float height;

    public float getBottomOffsetPixels() {
        return bottomOffsetPixels;
    }

    public float getTopOffsetPixels() {
        return topOffsetPixels;
    }

    protected final float bottomOffsetPixels;
    protected final float topOffsetPixels;

    private final float xScaleValue;
    private final float yScaleValue;

    public ChartViewPort(Date xmin, Date xmax, int ymin, int ymax, float width, float height, float topOffsetPixels, float bottomOffsetPixels) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.width = width;
        this.height = height;
        this.topOffsetPixels = topOffsetPixels;
        this.bottomOffsetPixels = bottomOffsetPixels;

        if (width > 0) {
            xScaleValue = (convertDateToFloat(xmax) - convertDateToFloat(xmin)) / width;
        } else {
            xScaleValue = 0;
        }

        if (getDrawAreaHeight() > 0) {
            yScaleValue = (convertIntegerToFloat(ymax) - convertIntegerToFloat(ymin)) / getDrawAreaHeight();
        } else {
            yScaleValue = 0;
        }
    }

    public Date getXmin() {
        return xmin;
    }

    public Date getXmax() {
        return xmax;
    }

    public int getYmin() {
        return ymin;
    }

    public int getYmax() {
        return ymax;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private float getDrawAreaHeight(){
        return height - topOffsetPixels - bottomOffsetPixels;
    }

    public PointF getCenter() {
        return new PointF(width / 2, height / 2);
    }

    public float xValueToPixels(Date v) {
        if (xScaleValue > 0) return (convertDateToFloat(v) - convertDateToFloat(xmin)) / xScaleValue;
        return 0;
    }

    public Date xPixelsToValue(float pixels) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long) (convertDateToFloat(xmin) + pixels * xScaleValue));
        return c.getTime();
    }

    public float yValueToPixels(Integer v) {
        if (yScaleValue > 0)
            return height - bottomOffsetPixels - (convertIntegerToFloat(v) - convertIntegerToFloat(ymin)) / yScaleValue;
        return 0;
    }

    public Integer yPixelsToValue(float pixels) {
        return ymin + Math.round((height - bottomOffsetPixels - pixels) * yScaleValue);
    }

    private float convertDateToFloat(Date date){
        if (date == null) return 0;
        return date.getTime();
    }

    private float convertIntegerToFloat(Integer v){
        return v;
    }


    public float xPixelsToOtherViewPort(float x, ChartViewPort other) {
        if (xScaleValue > 0)
            return (x / xScaleValue + convertDateToFloat(xmin) - convertDateToFloat(other.xmin))*other.xScaleValue;
        return 0;
    }
}
