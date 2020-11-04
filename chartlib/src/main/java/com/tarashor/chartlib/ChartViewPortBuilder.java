package com.tarashor.chartlib;

import java.util.Date;

public class ChartViewPortBuilder {
    private Date xmin;
    private Date xmax;
    private int ymin;
    private int ymax;

    private float width;
    private float height;

    protected float bottomOffsetPixels = 0.f;
    protected float topOffsetPixels = 0.f;


    public ChartViewPortBuilder setXmin(Date xmin) {
        this.xmin = xmin;
        return this;
    }

    public ChartViewPortBuilder setXmax(Date xmax) {
        this.xmax = xmax;
        return this;
    }

    public ChartViewPortBuilder setYmin(int ymin) {
        this.ymin = ymin;
        return this;
    }

    public ChartViewPortBuilder setYmax(int ymax) {
        this.ymax = ymax;
        return this;
    }

    public ChartViewPortBuilder setWidth(float width) {
        this.width = width;
        return this;
    }

    public ChartViewPortBuilder setHeight(float height) {
        this.height = height;
        return this;
    }

    public ChartViewPortBuilder setBottomOffsetPixels(float bottomOffsetPixels) {
        this.bottomOffsetPixels = bottomOffsetPixels;
        return this;
    }

    public ChartViewPortBuilder setTopOffsetPixels(float topOffsetPixels) {
        this.topOffsetPixels = topOffsetPixels;
        return this;
    }

    public ChartViewPort build(){
        return new ChartViewPort(xmin, xmax, ymin, ymax, width, height, topOffsetPixels, bottomOffsetPixels);
    }
}
