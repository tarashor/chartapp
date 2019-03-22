package com.tarashor.chartlib.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tarashor.chartlib.ChartViewPort;

import java.util.ArrayList;
import java.util.List;

class YAxis {
    private static final int GRID_HORIZONTAL_LINE_COUNT = 6;
    private float mTopLineOffsetPixels;
    private final Paint mGridPaint;
    private final Paint mTextPaint;

    private List<AxisMark> ticks;
    private int textHeight;
    private ChartViewPort mViewPort;

    public YAxis(ChartViewPort viewPort,
                 float topLineOffsetPixels, Paint gridPaint, Paint textPaint, IntegerValueFormatter valueFormatter) {
        mViewPort = viewPort;
        mTopLineOffsetPixels = topLineOffsetPixels;

        mGridPaint = gridPaint;
        mTextPaint = textPaint;

        Rect bounds = new Rect();
        mTextPaint.getTextBounds("9", 0, 1, bounds);
        textHeight = bounds.top - bounds.bottom;

        float bottomY = mViewPort.getHeight() - mViewPort.getBottomOffsetPixels();

        float delta = (bottomY  - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);

        ticks = new ArrayList<>();
        for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
            float y = bottomY - delta * i;
            int v = mViewPort.yPixelsToValue(y);
            AxisMark tick = new AxisMark(valueFormatter.format(v), 0, y);
            ticks.add(tick);
        }
    }

    public void draw(Canvas canvas){
        for (AxisMark tick : ticks) {
            float y = tick.getPixelOffsetY();
            canvas.drawLine(0, y, mViewPort.getWidth(), y, mGridPaint);
            canvas.drawText(tick.getText(), 0, y + textHeight + 10, mTextPaint);
        }
    }
}
