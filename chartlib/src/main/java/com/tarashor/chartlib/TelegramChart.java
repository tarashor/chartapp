package com.tarashor.chartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.Comparator;

public class TelegramChart extends Chart<ChartData> {
    private static final int GRID_HORIZONTAL_LINE_COUNT = 6;
    private float[] line;
    private Matrix transformMatrix;
    private Paint linePaint;
    private float xmin;
    private float xmax;
    private float ymin;
    private float ymax;

    public TelegramChart(Context context) {
        super(context);
    }

    public TelegramChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TelegramChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
    }

    @Override
    public void notifyDataSetChanged() {
        if (!isEmpty()) {
            calculateTransformMatrix();
            line = convertPointsToLine(mData.getPoints(0));
            linePaint.setColor(mData.getColor(0));
        }

        invalidate();
    }

    @Override
    protected void calculateOffsets() {

    }

    @Override
    protected void calcMinMax() {
        xmin = mData.convertXtoFloat(mData.getXMin());
        xmax = mData.convertXtoFloat(mData.getXMax());
        ymin = mData.convertYtoFloat(mData.getYMin(0));
        ymax = mData.convertYtoFloat(mData.getYMax(0));
    }

    private float[] convertPointsToLine(PointF[] points) {
        Arrays.sort(points, new Comparator<PointF>() {
            @Override
            public int compare(PointF o1, PointF o2) {
                return Float.compare(o1.x, o2.x);
            }
        });

        float[] line = new float[(points.length - 1) * 2 * 2];

        for (int i = 0; i < points.length - 1; i++){
            PointF start = convertToLocalCoordinates(points[i]);
            PointF end = convertToLocalCoordinates(points[i + 1]);
            line[4 * i] = start.x;
            line[4 * i + 1] = start.y;
            line[4 * i + 2] = end.x;
            line[4 * i + 3] = end.y;
        }

        return line;
    }


    private void calculateTransformMatrix() {
        transformMatrix = new Matrix();
        transformMatrix.setTranslate(-xmin, 0);
        float sx = getChartAreaWidth() / (xmax - xmin) ;
        float sy = getChartAreaHeight() / (ymax);
        transformMatrix.postScale(sx, -sy);
        transformMatrix.postTranslate(0, getChartAreaHeight());
    }

    private PointF convertToLocalCoordinates(PointF point) {
        float[] screenPoint = new float[2];
        transformMatrix.mapPoints(screenPoint, new float[]{point.x, point.y});
        return new PointF(screenPoint[0], screenPoint[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float delta = (getChartAreaHeight() - Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP)) / (GRID_HORIZONTAL_LINE_COUNT - 1);


        for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
            float y = getChartAreaHeight() - delta * i;
            canvas.drawLine(0, y, getChartAreaWidth(), y, mGridPaint);
            //canvas.drawText();
        }

        if (line != null) {
            canvas.drawLines(line, linePaint);
        }
    }
}
