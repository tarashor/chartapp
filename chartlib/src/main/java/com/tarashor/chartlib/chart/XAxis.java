package com.tarashor.chartlib.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.tarashor.chartlib.ChartViewPort;

import java.util.Date;

class XAxis {
    private final Paint mTextPaint;
    private final float minDistanceBetweenMarks;
    private final DateValueFormatter mValueConverter;
    private final float markMargin;

    private ChartViewPort mViewPort;

    private float currentDistanceBetweenMarks;
    private float firstMark;
    private float xMinPixels;
    private float xMaxPixels;
    private float lastMark;


    public XAxis(Paint textPaint, DateValueFormatter valueConverter) {
        mValueConverter = valueConverter;
        mTextPaint = textPaint;
        String sampleMarkText = "MMM MM";//valueFormatter.format(sample);
        Rect bound = new Rect();
        textPaint.getTextBounds(sampleMarkText, 0, sampleMarkText.length(), bound);
        float oneXMarkMinWidth = Math.abs(bound.right - bound.left);
        markMargin = 0;//oneXMarkMinWidth * 0.1f;
        float oneXMarkHeight = Math.abs(bound.top - bound.bottom);

        minDistanceBetweenMarks = oneXMarkMinWidth + markMargin;

        currentDistanceBetweenMarks = minDistanceBetweenMarks;
//
//        int numberOfMarksOnScreen = (int) (Math.ceil((mViewPort.getWidth() + oneXMarkMargin) / (oneXMarkMinWidth*1.5f  + oneXMarkMargin)));
//        minDistanceBetweenMarks = mViewPort.getWidth() / (numberOfMarksOnScreen - 1);
//        markY = viewPort.getHeight();// - oneXMarkHeight;
//
//        marksX = new float[numberOfMarksOnScreen];
//        for (int i = 0; i < numberOfMarksOnScreen; i++){
//            marksX[i] = i * minDistanceBetweenMarks;
//        }
    }

    public void viewPortChanged(ChartViewPort viewPort, Date xmin, Date xmax) {
//        for (int i = 0; i < marksX.length; i++){
//            marksX[i] = mViewPort.xPixelsToOtherViewPort(marksX[i], viewPort);
//        }
        if (mViewPort == null || !mViewPort.isValid()) {
            if (viewPort != null && viewPort.isValid()) {
                int maxNumberOfMarksOnScreen = (int) (Math.ceil((viewPort.getWidth()) / (minDistanceBetweenMarks))) - 1;
                currentDistanceBetweenMarks = (viewPort.getWidth() - minDistanceBetweenMarks) / (maxNumberOfMarksOnScreen);
            }
        } else {
            if (viewPort != null) {
                currentDistanceBetweenMarks = mViewPort.xPixelsDistanceToOtherViewPort(currentDistanceBetweenMarks, viewPort);
                if (currentDistanceBetweenMarks >= 2 * minDistanceBetweenMarks) {
                    //start animation to fade in each second
                    currentDistanceBetweenMarks = currentDistanceBetweenMarks / 2;
                } else {
                    if (currentDistanceBetweenMarks < minDistanceBetweenMarks) {
                        //start animation to fade out each second
                        currentDistanceBetweenMarks = currentDistanceBetweenMarks * 2;
                    }
                }
                Log.v("XAXIS", "currentDistanceBetweenMarks = " + currentDistanceBetweenMarks);
            }
        }
        mViewPort = viewPort;

        if (mViewPort != null && mViewPort.isValid()) {
            xMinPixels = mViewPort.xValueToPixels(xmin);
            xMaxPixels = mViewPort.xValueToPixels(xmax);
            firstMark = xMinPixels + currentDistanceBetweenMarks + minDistanceBetweenMarks*0.5f;
            lastMark = xMaxPixels - currentDistanceBetweenMarks - minDistanceBetweenMarks*0.5f;
            Log.v("XAXIS", "firstMark = " + firstMark);
            Log.v("XAXIS", "lastmark = " + lastMark);

//            int numberOfMarks = Math.round((lastMark - firstMark) / currentDistanceBetweenMarks) + 1;
//            for (int i = 0; i < numberOfMarks; i++) {
//                float x = firstMark + i * currentDistanceBetweenMarks;
//                Log.v("XAXIS marks", "x = " + x);
//            }
        }

    }


    public void draw(Canvas canvas){
        if (mViewPort != null && mViewPort.isValid()) {
            if (xMinPixels >= 0) {
                mTextPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(xMinPixels)), xMinPixels, mViewPort.getHeight(), mTextPaint);
            }

            mTextPaint.setTextAlign(Paint.Align.CENTER);
//            for (float x = firstMark; x <= lastMark; x += currentDistanceBetweenMarks) {
//                if (x >= 0 && x <= mViewPort.getWidth()) {
//                    canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(x)), x, mViewPort.getHeight(), mTextPaint);
//                }
//            }
            int numberOfMarks = Math.round((lastMark - firstMark) / currentDistanceBetweenMarks) + 1;
            for (int i = 0; i < numberOfMarks; i++) {
                float x = firstMark + i * currentDistanceBetweenMarks;
                Log.v("XAXIS marks", "x = " + x);
                //if (x >= 0 && x <= mViewPort.getWidth()) {
                    canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(x)), x, mViewPort.getHeight(), mTextPaint);
                //}
            }

            if (xMaxPixels <= mViewPort.getWidth()) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(xMaxPixels)), xMaxPixels, mViewPort.getHeight(), mTextPaint);
            }
        }

//
//        for (int i = 0; i < marksX.length; i++){
//            float x = marksX[i];
//            if (x >= 0){
//                if (i == 0){
//                    mTextPaint.setTextAlign(Paint.Align.LEFT);
//                } else if (i == marksX.length - 1){
//                    mTextPaint.setTextAlign(Paint.Align.RIGHT);
//                } else {
//                    mTextPaint.setTextAlign(Paint.Align.CENTER);
//                }
//                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(x)), x, markY, mTextPaint);
//            }
//        }
    }
}
