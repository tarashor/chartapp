package com.tarashor.chartlib.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tarashor.chartlib.ChartViewPort;
import com.tarashor.chartlib.IValueFormatter;

class XAxis {
    private ChartViewPort mViewPort;

    private final Paint mTextPaint;
    private final float markWidth;
    private final float markY;
    private final DateValueFormatter mValueConverter;

    private float[] marksX;


    public XAxis(ChartViewPort viewPort, Paint textPaint, DateValueFormatter valueConverter) {
        mViewPort = viewPort;
        mValueConverter = valueConverter;
        mTextPaint = textPaint;
        String sampleMarkText = "MMM MM";//valueFormatter.format(sample);
        Rect bound = new Rect();
        textPaint.getTextBounds(sampleMarkText, 0, sampleMarkText.length(), bound);
        float oneXMarkMinWidth = Math.abs(bound.right - bound.left);
        float oneXMarkMargin = oneXMarkMinWidth * 0.2f;
        float oneXMarkHeight = Math.abs(bound.top - bound.bottom);

        int numberOfMarksOnScreen = (int) (Math.ceil((mViewPort.getWidth() + oneXMarkMargin) / (oneXMarkMinWidth*1.5f  + oneXMarkMargin)));
        markWidth = mViewPort.getWidth() / (numberOfMarksOnScreen - 1);
        markY = viewPort.getHeight() - oneXMarkHeight;

        marksX = new float[numberOfMarksOnScreen];
        for (int i = 0; i < numberOfMarksOnScreen; i++){
            marksX[i] = i * markWidth;
        }
    }

    public void viewPortChanged(ChartViewPort viewPort){
        for (int i = 0; i < marksX.length; i++){
            marksX[i] = mViewPort.xPixelsToOtherViewPort(marksX[i], viewPort);
        }
        mViewPort = viewPort;
//        xMinP = mValueConverter.valueToPixels(xMin);
//        firstMarkOnScreenX =  xMinP + ((int)(Math.abs(xMinP) / markWidth) + 1) * markWidth;
    }


    public void draw(Canvas canvas){
//
//        if (xMinP >=0) {
//            mTextPaint.setTextAlign(Paint.Align.LEFT);
//            canvas.drawText(mValueConverter.format(mValueConverter.pixelsToValue(xMinP)), xMinP, markY, mTextPaint);
//        }
//
//
//        for (float x = firstMarkOnScreenX; x < mChartAreaWidth; x += markWidth) {
//            canvas.drawText(mValueConverter.format(mValueConverter.pixelsToValue(x)), x, markY, mTextPaint);
//        }

        for (int i = 0; i < marksX.length; i++){
            float x = marksX[i];
            if (x >= 0){
                if (i == 0){
                    mTextPaint.setTextAlign(Paint.Align.LEFT);
                } else if (i == marksX.length - 1){
                    mTextPaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                }
                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(x)), x, markY, mTextPaint);
            }
        }
    }
}
