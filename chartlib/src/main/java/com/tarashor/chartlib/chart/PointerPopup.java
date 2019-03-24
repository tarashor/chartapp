package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.tarashor.chartlib.BaseChartView;
import com.tarashor.chartlib.ChartViewPort;
import com.tarashor.chartlib.Utils;

import java.util.Map;

class PointerPopup {
    private final float mTopBottomMargin;
    private final float mLeftRightMargin;
    private final float mHeaderBottomMargin;
    private final float mValueBottomMargin;
    private final float mValueBetweenMargin;


    RectF rect;
    private float mCurrentPointer;
    private Map<String, BaseChartView.DateToIntDataPoint> mPoints;
    private ChartViewPort mViewPort;
    private Map<String, Integer> mLineColors;
    private Paint mPopupBackgroundPaint;
    private Paint mPopupBorderPaint;
    private Paint mPopupHeaderTextPaint;
    private Paint mValuesTextPaint;
    private Paint mDescrTextPaint;

    private float headerY;
    private float valueY;
    private final float descrY;
    private final float columnWidth;
    
    private String headerText;

    public PointerPopup(Context context, float currentPointer,
                        Map<String, BaseChartView.DateToIntDataPoint> points,
                        ChartViewPort viewPort,
                        Map<String, Integer> mLineColors,
                        Paint mPopupBackgroundPaint,
                        Paint mPopupBorderPaint,
                        Paint mPopupHeaderTextPaint,
                        Paint mValuesTextPaint,
                        Paint mDescrTextPaint) {

        mCurrentPointer = currentPointer;
        mPoints = points;
        mViewPort = viewPort;
        this.mLineColors = mLineColors;
        this.mPopupBackgroundPaint = mPopupBackgroundPaint;
        this.mPopupBorderPaint = mPopupBorderPaint;
        this.mPopupHeaderTextPaint = mPopupHeaderTextPaint;
        this.mValuesTextPaint = mValuesTextPaint;
        this.mDescrTextPaint = mDescrTextPaint;

        mTopBottomMargin = Utils.convertDpToPixel(context, 6);
        mLeftRightMargin = Utils.convertDpToPixel(context, 12);

        mHeaderBottomMargin = Utils.convertDpToPixel(context, 5);
        mValueBottomMargin = Utils.convertDpToPixel(context, 2);
        mValueBetweenMargin = Utils.convertDpToPixel(context, 6);

        rect = new RectF();
        rect.top = mViewPort.getTopOffsetPixels();

        float headerHeight = getTextHeight(mPopupHeaderTextPaint);

        headerY = rect.top + mTopBottomMargin + headerHeight;

        float valueHeight = getTextHeight(mValuesTextPaint);

        float descrHeight = getTextHeight(mDescrTextPaint);
        
        valueY = headerY + mHeaderBottomMargin + valueHeight;
        
        descrY = valueY + mValueBottomMargin + descrHeight;
        
        rect.bottom = descrY + 3*mTopBottomMargin;

        headerText = DateValueFormatter.formatHeader(mViewPort.xPixelsToValue(currentPointer));
        
        float headerWidth = getTextWidth("MMM, MMM MM", mPopupHeaderTextPaint);

        float maxColumnPointWidth = 0;

        float valueWidth = getTextWidth(String.valueOf(viewPort.getYmax()), mValuesTextPaint);

        for (String lineDescr : points.keySet()){
            float descrWidth = getTextWidth(lineDescr, mDescrTextPaint);
            float columnWidth = Math.max(valueWidth, descrWidth);
            if (maxColumnPointWidth < columnWidth) maxColumnPointWidth = columnWidth;
        }

        columnWidth = maxColumnPointWidth;

        int numberOfValue = points.size();

        float contentWidth = Math.max(maxColumnPointWidth *numberOfValue +
                        mValueBetweenMargin*(numberOfValue-1), headerWidth);


        if (currentPointer  - contentWidth / 2 - mLeftRightMargin - mPopupBorderPaint.getStrokeWidth() < 0){
            rect.left = mPopupBorderPaint.getStrokeWidth();
            rect.right = rect.left + contentWidth  + 2 * mLeftRightMargin;
        } else if (currentPointer  + contentWidth / 2 + mLeftRightMargin > mViewPort.getWidth() - mPopupBorderPaint.getStrokeWidth() ) {
            rect.right = mViewPort.getWidth() - mPopupBorderPaint.getStrokeWidth();
            rect.left = rect.right - contentWidth - 2*mLeftRightMargin;
        } else {
            rect.left = currentPointer - contentWidth / 2 - mLeftRightMargin;
            rect.right = rect.left + contentWidth + 2 * mLeftRightMargin;
        }

    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetricsHeader = paint.getFontMetrics();
        return Math.abs(fontMetricsHeader.top - fontMetricsHeader.bottom);
    }

    private float getTextWidth(String text, Paint paint) {
        if (text == null || paint == null) return 0;
        return paint.measureText(text);
    }

    public void draw(Canvas canvas) {
        canvas.drawRoundRect(rect, 5,5, mPopupBackgroundPaint);
        canvas.drawRoundRect(rect, 5,5, mPopupBorderPaint);

        canvas.drawText(headerText, rect.left+mLeftRightMargin, headerY, mPopupHeaderTextPaint);

        float x = rect.left+mLeftRightMargin;
        for (String lineDescr : mPoints.keySet()){
            mValuesTextPaint.setColor(mLineColors.get(lineDescr));
            mDescrTextPaint.setColor(mLineColors.get(lineDescr));
            String valueText = mPoints.get(lineDescr).getY().toString();

            canvas.drawText(valueText, x, valueY, mValuesTextPaint);
            canvas.drawText(lineDescr, x, descrY, mDescrTextPaint);

            x+=columnWidth + mValueBetweenMargin;
        }

    }
}
