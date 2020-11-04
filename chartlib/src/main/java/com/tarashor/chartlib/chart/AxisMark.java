package com.tarashor.chartlib.chart;

class AxisMark {
    private String text;
    private float pixelOffsetX;
    private float pixelOffsetY;

    AxisMark(String text, float pixelOffsetX, float pixelOffsetY) {
        this.text = text;
        this.pixelOffsetY = pixelOffsetY;
        this.pixelOffsetX = pixelOffsetX;
    }

    float getPixelOffsetX() {
        return pixelOffsetX;
    }
    float getPixelOffsetY() {
        return pixelOffsetY;
    }
    String getText() {
        return text;
    }
}
