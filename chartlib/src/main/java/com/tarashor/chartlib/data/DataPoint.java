package com.tarashor.chartlib.data;

public class DataPoint<XType extends Comparable<XType>, YType extends Comparable<YType>> implements Comparable<DataPoint<XType, YType>> {
    private final XType x;
    private final YType y;

    public DataPoint(XType x, YType y) {
        this.x = x;
        this.y = y;
    }

    public XType getX() {
        return x;
    }

    public YType getY() {
        return y;
    }

    @Override
    public int compareTo(DataPoint<XType, YType> o) {
        if (o == null) return 1;
        return x.compareTo(o.x);
    }
}
