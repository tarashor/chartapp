package com.tarashor.chartlib;

public final class Point<XType extends Comparable<XType>, YType extends Comparable<YType>> {
    private final XType x;
    private final YType y;

    public Point(XType x, YType y) {
        this.x = x;
        this.y = y;
    }

    public XType getX() {
        return x;
    }

    public YType getY() {
        return y;
    }
}
