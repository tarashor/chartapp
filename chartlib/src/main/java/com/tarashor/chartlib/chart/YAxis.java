package com.tarashor.chartlib.chart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.tarashor.chartlib.ChartViewPort;

import java.util.ArrayList;
import java.util.List;

class YAxis implements ValueAnimator.AnimatorUpdateListener {
    private static final int GRID_HORIZONTAL_LINE_COUNT = 6;
    private View mView;
    private float mTopLineOffsetPixels;
    private final Paint mGridPaint;
    private final Paint mTextPaint;
    private IntegerValueFormatter mIntegerValueFormatter;

    private int textHeight;

    private List<AxisMark> newTicks;
    private List<AxisMark> oldTicks;
    private ChartViewPort newViewPort;
    private ChartViewPort oldViewPort;

    private float scaleFactor;

    private ValueAnimator mAnimator;


    public YAxis(View view, float topLineOffsetPixels, Paint gridPaint, Paint textPaint, IntegerValueFormatter integerValueFormatter) {
        mView = view;
        mTopLineOffsetPixels = topLineOffsetPixels;

        mGridPaint = gridPaint;
        mTextPaint = textPaint;
        mIntegerValueFormatter = integerValueFormatter;

        Rect bounds = new Rect();
        mTextPaint.getTextBounds("9", 0, 1, bounds);
        textHeight = bounds.top - bounds.bottom;

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(10000);
        mAnimator.setFloatValues(0, 1);
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation, boolean isReverse) {
//
//            }
//        });
        mAnimator.addUpdateListener(this);
    }


    public void viewPortChanged(ChartViewPort viewPort) {

        mAnimator.cancel();
        oldViewPort = newViewPort;
        newViewPort = viewPort;

        oldTicks = newTicks;

        if (viewPort != null && viewPort.isValid()){
            float bottomY = newViewPort.getHeight() - newViewPort.getBottomOffsetPixels();
            float delta = (bottomY  - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);

            newTicks = new ArrayList<>();
            for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
                float y = bottomY - delta * i;
                int v = newViewPort.yPixelsToValue(y);
                AxisMark tick = new AxisMark(mIntegerValueFormatter.format(v), 0, y);
                newTicks.add(tick);
            }
        }

//        oldViewPort.getYmax() = y1
//        newViewPort.getYmax() = y2
//                y2 = newViewPort.getYmax()/oldViewPort.getYmax()*y1
//                (newViewPort.getYmax() - oldViewPort.getYmax()) x + oldViewPort.getYmax()

        mAnimator.start();

    }

    public void draw(Canvas canvas) {
        if (oldTicks != null) {
            for (AxisMark tick : oldTicks) {
                float y = tick.getPixelOffsetY();
                y = y *scaleFactor*newViewPort.getYmax()/oldViewPort.getYmax();
                mGridPaint.setAlpha((int) (255*scaleFactor));
                mTextPaint.setAlpha((int) (255*scaleFactor));
                canvas.drawLine(0, y, oldViewPort.getWidth(), y, mGridPaint);
                canvas.drawText(tick.getText(), 0, y + textHeight + 10, mTextPaint);
            }
        }

        if (newTicks != null) {
            for (AxisMark tick : newTicks) {
                float y = tick.getPixelOffsetY();
                y = y *(1-scaleFactor)*oldViewPort.getYmax()/newViewPort.getYmax();
                mGridPaint.setAlpha((int) (255*(1-scaleFactor)));
                mTextPaint.setAlpha((int) (255*(1-scaleFactor)));
                canvas.drawLine(0, y, newViewPort.getWidth(), y, mGridPaint);
                canvas.drawText(tick.getText(), 0, y + textHeight + 10, mTextPaint);
            }
        }

//        float bottomY = newViewPort.getHeight() - newViewPort.getBottomOffsetPixels();
//        float delta = (bottomY - newViewPort.getTopOffsetPixels() - mTopLineOffsetPixels) / (GRID_HORIZONTAL_LINE_COUNT - 1);
//
//        //newTicks = new ArrayList<>();
//        for (int i = 0; i < GRID_HORIZONTAL_LINE_COUNT; i++) {
//            float y = bottomY - delta * i;
//            int v = newViewPort.yPixelsToValue(y);
//            canvas.drawLine(0, y, newViewPort.getWidth(), y, mGridPaint);
//            canvas.drawText(mIntegerValueFormatter.format(v), 0, y + textHeight + 10, mTextPaint);
//        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        scaleFactor = animation.getAnimatedFraction();
        mView.invalidate();
    }
}
