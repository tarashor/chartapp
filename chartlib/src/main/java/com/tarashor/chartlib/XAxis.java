package com.tarashor.chartlib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

class XAxis<T> {
    private final float oneXMarkWidth;
    private final float oneXMarkHeight;

    final int numberOfMarks;
    private List<AxisMark> ticks = new ArrayList<>();
    private Paint mTextPaint;

    public XAxis(float chartAreaWidth, float chartAreaBottom, float textAreaHeight, Paint textPaint, IValueConverter<T> valueFormatter, T sample) {
        mTextPaint = textPaint;
        String sampleMarkText = valueFormatter.format(sample);
        Rect bound = new Rect();
        textPaint.getTextBounds(sampleMarkText, 0, sampleMarkText.length(), bound);
        oneXMarkWidth = Math.abs(bound.right - bound.left) * 1.5f;
        oneXMarkHeight = Math.abs(bound.top - bound.bottom);
        numberOfMarks = Math.round(chartAreaWidth / oneXMarkWidth);
        for (int i = 0; i < numberOfMarks; i++)
        {
            float x = oneXMarkWidth * i;
            T v = valueFormatter.pixelsToValue(x);
            AxisMark tick = new AxisMark(valueFormatter.format(v), x, chartAreaBottom + (textAreaHeight + oneXMarkHeight) / 2);
            ticks.add(tick);
        }
    }

    public void draw(Canvas canvas){
        for (AxisMark tick : ticks) {
            canvas.drawText(tick.getText(), tick.getPixelOffsetX(), tick.getPixelOffsetY(), mTextPaint);
        }
    }
}
