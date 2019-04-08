package com.tarashor.chartlib.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tarashor.chartlib.ChartViewPort;

class YAxis {
    private static final int GRID_HORIZONTAL_LINE_COUNT = 6;
    private float mTopLineOffsetPixels;
    private final Paint mGridPaint;
    private final Paint mTextPaint;
    private IntegerValueFormatter mIntegerValueFormatter;

    private int textHeight;
    private ChartViewPort viewPort;
    private float bottomY;
    private float delta;

    public YAxis(float topLineOffsetPixels, Paint gridPaint, Paint textPaint, IntegerValueFormatter integerValueFormatter) {
        mTopLineOffsetPixels = topLineOffsetPixels;

        mGridPaint = gridPaint;
        mTextPaint = textPaint;
        mIntegerValueFormatter = integerValueFormatter;

        Rect bounds = new Rect();
        mTextPaint.getTextBounds("9", 0, 1, bounds);
        textHeight = bounds.top - bounds.bottom;
    }


    public void viewPortChangedAndCalculate(ChartViewPort viewPort) {
        this.viewPort = viewPort;
        bottomY = viewPort.getHeight() - viewPort.getBottomOffsetPixels();
        delta = (bottomY - viewPort.getTopOffsetPixels() - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);
    }

    public void draw(Canvas canvas, float animationFactor) {
        //newTicks = new ArrayList<>();
        for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
            float y = bottomY - delta * i;
            int v = viewPort.yPixelsToValue(y);
            mGridPaint.setAlpha((int) (255 * animationFactor));
            mTextPaint.setAlpha((int) (255 * animationFactor));
            canvas.drawLine(0, y, viewPort.getWidth(), y, mGridPaint);
            canvas.drawText(mIntegerValueFormatter.format(v), 0, y + textHeight + 10, mTextPaint);
        }
    }
}
