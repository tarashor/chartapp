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
    private IntegerValueFormatter mIntegerValueFormatter;

    private List<AxisMark> newTicks;
    private List<AxisMark> currentTicks;
    private int textHeight;
    private ChartViewPort newViewPort;
    private ChartViewPort oldViewPort;


    public YAxis(float topLineOffsetPixels, Paint gridPaint, Paint textPaint, IntegerValueFormatter integerValueFormatter) {
        mTopLineOffsetPixels = topLineOffsetPixels;

        mGridPaint = gridPaint;
        mTextPaint = textPaint;
        mIntegerValueFormatter = integerValueFormatter;

        Rect bounds = new Rect();
        mTextPaint.getTextBounds("9", 0, 1, bounds);
        textHeight = bounds.top - bounds.bottom;
    }


    public void viewPortChanged(ChartViewPort viewPort) {
        newViewPort = viewPort;

//        if (viewPort != null && viewPort.isValid()){
//
//            float bottomY = viewPort.getHeight() - viewPort.getBottomOffsetPixels();
//            float delta = (bottomY  - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);
//
//            newTicks = new ArrayList<>();
//            for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
//                float y = bottomY - delta * i;
//                int v = viewPort.yPixelsToValue(y);
//                AxisMark tick = new AxisMark(mIntegerValueFormatter.format(v), 0, y);
//                newTicks.add(tick);
//            }
//        }
    }

    public void draw(Canvas canvas) {
//        if (currentTicks != null) {
//            for (AxisMark tick : currentTicks) {
//                float y = tick.getPixelOffsetY();
//                canvas.drawLine(0, y, mViewPort.getWidth(), y, mGridPaint);
//                canvas.drawText(tick.getText(), 0, y + textHeight + 10, mTextPaint);
//            }
//        }
//
//        if (newTicks != null) {
//            for (AxisMark tick : newTicks) {
//                float y = tick.getPixelOffsetY();
//                canvas.drawLine(0, y, mViewPort.getWidth(), y, mGridPaint);
//                canvas.drawText(tick.getText(), 0, y + textHeight + 10, mTextPaint);
//            }
//        }

        float bottomY = newViewPort.getHeight() - newViewPort.getBottomOffsetPixels();
        float delta = (bottomY - newViewPort.getTopOffsetPixels() - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);

        //newTicks = new ArrayList<>();
        for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
            float y = bottomY - delta * i;
            int v = newViewPort.yPixelsToValue(y);
            canvas.drawLine(0, y, newViewPort.getWidth(), y, mGridPaint);
            canvas.drawText(mIntegerValueFormatter.format(v), 0, y + textHeight + 10, mTextPaint);
        }
    }
}
