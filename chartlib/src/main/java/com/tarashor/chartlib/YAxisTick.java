package com.tarashor.chartlib;

import java.util.List;

class AxisMark {
    private String text;
    private float pixelOffsetX;
    private float pixelOffsetY;

    public AxisMark(String text, float pixelOffsetX, float pixelOffsetY) {
        this.text = text;
        this.pixelOffsetY = pixelOffsetY;
        this.pixelOffsetX = pixelOffsetX;
    }

    public float getPixelOffsetX() {
        return pixelOffsetX;
    }
    public float getPixelOffsetY() {
        return pixelOffsetY;
    }

    public String getText() {
        return text;
    }
}
