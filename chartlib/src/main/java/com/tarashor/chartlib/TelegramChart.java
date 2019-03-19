package com.tarashor.chartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.Comparator;

public class TelegramChart extends Chart<DateToIntChartData> {
    private float[] line;
    private Matrix transformToScreenMatrix;
    private Matrix transformToRealMatrix;
    private Paint linePaint;
    private float xmin;
    private float xmax;
    private float ymin;
    private float ymax;

    private float topRealOffset;
    private float topPixelsOffset;

    private YAxis yAxis;

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

        topPixelsOffset = Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP);
    }


    @Override
    public void notifyDataSetChanged() {
        if (!isEmpty()) {
            line = convertPointsToLine(mData.getPoints(0));
            linePaint.setColor(mData.getColor(0));
        }

        yAxis = new YAxis(ymin, ymax,
                getChartAreaWidth(), getChartAreaBottom(), topPixelsOffset,
                mGridPaint, mTextPaint);

        invalidate();
    }

    @Override
    protected void calculateOffsets() {

    }

    @Override
    protected void calcMinMax() {
        xmin = mData.convertXtoFloat(mData.getXMin());
        xmax = mData.convertXtoFloat(mData.getXMax());
        ymin = 0;//mData.convertYtoFloat(mData.getYMin(0));
        ymax = getRealTop(mData.getYMax(0));

    }

    private float getRealTop(int yMax) {
        int div = 10;
        int preDiv = 1;

        while (yMax % div <=  (yMax / div * div) * topPixelsOffset / (getChartAreaBottom() - topPixelsOffset)) {
            preDiv = div;
            div *= 10;
        }

        int maxHorizontalLine = yMax / preDiv * preDiv;
        if (preDiv == 1) maxHorizontalLine = (yMax / 10 + 1)* 10;

        topRealOffset = maxHorizontalLine * topPixelsOffset / (getChartAreaBottom() - topPixelsOffset);

        return maxHorizontalLine + topRealOffset;

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

    @Override
    protected void calculateTransformMatrix() {
        transformToScreenMatrix = new Matrix();
        transformToScreenMatrix.setTranslate(-xmin, -ymin);
        float sx = getChartAreaWidth() / (xmax - xmin) ;
        float sy = getChartAreaBottom() / (ymax - ymin);
        transformToScreenMatrix.postScale(sx, -sy);
        transformToScreenMatrix.postTranslate(0, getChartAreaBottom());

//        transformToRealMatrix = new Matrix();
//        transformToRealMatrix.invert(transformToScreenMatrix);
    }

    private PointF convertToLocalCoordinates(PointF point) {
        float[] screenPoint = new float[2];
        transformToScreenMatrix.mapPoints(screenPoint, new float[]{point.x, point.y});
        return new PointF(screenPoint[0], screenPoint[1]);
    }

    private PointF convertToRealCoordinates(PointF point) {
        float[] realPoint = new float[2];
        transformToRealMatrix.mapPoints(realPoint, new float[]{point.x, point.y});
        return new PointF(realPoint[0], realPoint[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (line != null) {
            canvas.drawLines(line, linePaint);
        }
    }

    @Override
    protected void drawYAxis(Canvas canvas) {
        yAxis.draw(canvas);
    }
}
