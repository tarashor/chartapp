package com.tarashor.chartlib;

import java.util.List;

class YAxisTick {
    private String text;
    private float pixelOffset;

    public YAxisTick(String text, float pixelOffset) {
        this.text = text;
        this.pixelOffset = pixelOffset;
    }

    public float getPixelOffset() {
        return pixelOffset;
    }

    public String getText() {
        return text;
    }
}
