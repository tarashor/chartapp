package com.tarashor.chartlib.chart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.tarashor.chartlib.ChartViewPort;

import java.util.Date;

class XAxis implements ValueAnimator.AnimatorUpdateListener {
    private final Paint mTextPaint;
    private View mContainer;
    private final DateValueFormatter mValueConverter;

    private final float minDistanceBetweenMarks;
    private final float markMargin;

    private ChartViewPort mViewPort;

    private float currentDistanceBetweenMarks;
    private float firstMark;
    private float lastMark;
    private float xMinPixels;
    private float xMaxPixels;

    private int numberOfMarks;

    private ValueAnimator mAnimator;
    private int disapperAlpha = 255;


    public XAxis(Paint textPaint, DateValueFormatter valueConverter, View container) {
        mValueConverter = valueConverter;
        mTextPaint = textPaint;
        mContainer = container;
        String sampleMarkText = "MMM M";
        Rect bound = new Rect();
        textPaint.getTextBounds(sampleMarkText, 0, sampleMarkText.length(), bound);
        float oneXMarkMinWidth = Math.abs(bound.right - bound.left);
        markMargin = 0;//oneXMarkMinWidth * 0.1f;

        minDistanceBetweenMarks = oneXMarkMinWidth + markMargin;

//        mAnimator = new ValueAnimator();
//        mAnimator.setDuration(1000);
//        mAnimator.setIntValues(0, 255);
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation, boolean isReverse) {
//                if (isReverse) {
//                    numberOfMarks /= 2;
//                    numberOfMarks++;
//                } else {
//                    numberOfMarks *= 2;
//                    numberOfMarks--;
//                }
//            }
//        });
//        mAnimator.addUpdateListener(this);
    }

    public void viewPortChanged(ChartViewPort viewPort, Date xmin, Date xmax) {
        if (mViewPort == null || !mViewPort.isValid()) {
            if (viewPort != null && viewPort.isValid()) {
                numberOfMarks = (int) (Math.floor((viewPort.getWidth()) / (minDistanceBetweenMarks))) - 1;
            }
        }
        mViewPort = viewPort;

        if (mViewPort != null && mViewPort.isValid()) {
            xMinPixels = mViewPort.xValueToPixels(xmin);
            xMaxPixels = mViewPort.xValueToPixels(xmax);
            currentDistanceBetweenMarks = (xMaxPixels - xMinPixels - minDistanceBetweenMarks) / (numberOfMarks - 1);

            if (currentDistanceBetweenMarks >= 2 * minDistanceBetweenMarks) {
                numberOfMarks *= 2;
                numberOfMarks--;
                disapperAlpha = 255;
            } else if (currentDistanceBetweenMarks < minDistanceBetweenMarks) {
                numberOfMarks /= 2;
                numberOfMarks++;
                disapperAlpha = 255;
            }

            firstMark = xMinPixels + currentDistanceBetweenMarks + minDistanceBetweenMarks * 0.5f;
            lastMark = xMaxPixels - currentDistanceBetweenMarks - minDistanceBetweenMarks * 0.5f;
        }
    }


    public void draw(Canvas canvas){
        if (mViewPort != null && mViewPort.isValid()) {
            if (xMinPixels >= 0) {
                mTextPaint.setTextAlign(Paint.Align.LEFT);
                mTextPaint.setAlpha(255);
                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(xMinPixels)), xMinPixels, mViewPort.getHeight(), mTextPaint);
            }

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            int numberOfMarks = Math.round((lastMark - firstMark) / currentDistanceBetweenMarks) + 1;
            for (int i = 0; i < numberOfMarks; i++) {
                float x = firstMark + i * currentDistanceBetweenMarks;
                if (x >= 0 && x <= mViewPort.getWidth()) {
                    if (i % 2 == 0){
                        mTextPaint.setAlpha(disapperAlpha);
                    } else {
                        mTextPaint.setAlpha(255);
                    }
                    canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(x)), x, mViewPort.getHeight(), mTextPaint);
                }
            }

            if (xMaxPixels <= mViewPort.getWidth()) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                mTextPaint.setAlpha(255);
                canvas.drawText(mValueConverter.format(mViewPort.xPixelsToValue(xMaxPixels)), xMaxPixels, mViewPort.getHeight(), mTextPaint);
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        disapperAlpha = (int) animation.getAnimatedValue();
        mContainer.invalidate();
    }
}
