package com.tarashor.chartlib.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tarashor.chartlib.IValueConverter;

class XAxis<T> {
    private final AxisMark[] ticks;
    private Paint mTextPaint;

    public XAxis(float chartAreaWidth, float chartAreaBottom, float textAreaHeight,
                 Paint textPaint,
                 IValueConverter<T> valueFormatter, T sample) {

        mTextPaint = textPaint;
        String sampleMarkText = valueFormatter.format(sample);
        Rect bound = new Rect();
        textPaint.getTextBounds(sampleMarkText, 0, sampleMarkText.length(), bound);
        float oneXMarkMinWidth = Math.abs(bound.right - bound.left);
        float oneXMarkMargin = oneXMarkMinWidth*0.2f;
        float oneXMarkHeight = Math.abs(bound.top - bound.bottom);
        int numberOfMarks = (int) (Math.ceil((chartAreaWidth + oneXMarkMargin) / (oneXMarkMinWidth*1.5f  + oneXMarkMargin)) - 1);

        float oneXMarkWidth = chartAreaWidth / numberOfMarks;

        ticks = new AxisMark[numberOfMarks+1];
        for (int i = 0; i < numberOfMarks+1; i++) {
            float x = oneXMarkWidth * i;
            T v = valueFormatter.pixelsToValue(x);
            AxisMark tick = new AxisMark(valueFormatter.format(v), x, chartAreaBottom + (textAreaHeight + oneXMarkHeight) / 2);
            ticks[i] = tick;
        }
    }

    public void draw(Canvas canvas){
        for (int i = 0; i < ticks.length; i++) {
            AxisMark tick = ticks[i];
            if (i == 0){
                mTextPaint.setTextAlign(Paint.Align.LEFT);
            } else if (i == ticks.length - 1) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
            }else {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
            }
            canvas.drawText(tick.getText(), tick.getPixelOffsetX(), tick.getPixelOffsetY(), mTextPaint);
        }
    }
}
