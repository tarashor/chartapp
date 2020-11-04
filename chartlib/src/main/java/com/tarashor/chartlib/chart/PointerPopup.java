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


    private RectF rect;
    private Map<String, BaseChartView.DateToIntDataPoint> mPoints;

    private Map<String, Integer> mLineColors;
    private Paint mPopupBackgroundPaint;
    private Paint mPopupBorderPaint;
    private Paint mPopupHeaderTextPaint;
    private Paint mValuesTextPaint;
    private Paint mDescrTextPaint;

    private float headerY;
    private float valueY;
    private float descrY;

    private float topRectOffset;
    
    private String headerText;
    private float headerWidth;
    private float maxColumnPointWidth;

    public PointerPopup(Context context,
                        Paint mPopupBackgroundPaint,
                        Paint mPopupBorderPaint,
                        Paint mPopupHeaderTextPaint,
                        Paint mValuesTextPaint,
                        Paint mDescrTextPaint) {
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

        float headerHeight = getTextHeight(mPopupHeaderTextPaint);
        float valueHeight = getTextHeight(mValuesTextPaint);
        float descrHeight = getTextHeight(mDescrTextPaint);

        headerWidth = getTextWidth("MMM, MMM MM", mPopupHeaderTextPaint);

        rect = new RectF();
        rect.top = 0;

        headerY = rect.top + mTopBottomMargin + headerHeight;
        
        valueY = headerY + mHeaderBottomMargin + valueHeight;
        
        descrY = valueY + mValueBottomMargin + descrHeight;
        
        rect.bottom = descrY + 3*mTopBottomMargin;

    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetricsHeader = paint.getFontMetrics();
        return Math.abs(fontMetricsHeader.top - fontMetricsHeader.bottom);
    }

    private float getTextWidth(String text, Paint paint) {
        if (text == null || paint == null) return 0;
        return paint.measureText(text);
    }

    public void setCurrentPointer(float currentPointer,
                                  Map<String, BaseChartView.DateToIntDataPoint> points,
                                  Map<String, Integer> lineColors,
                                  ChartViewPort viewPort){

        mLineColors = lineColors;
        mPoints = points;

        topRectOffset = viewPort.getTopOffsetPixels();
        rect.top = topRectOffset;
        rect.bottom = descrY + 3*mTopBottomMargin + topRectOffset;

        headerText = DateValueFormatter.formatHeader(viewPort.xPixelsToValue(currentPointer));

        float valueWidth = getTextWidth(String.valueOf(viewPort.getYmax()), mValuesTextPaint);

        maxColumnPointWidth = 0;
        for (String lineDescr : points.keySet()){
            float descrWidth = getTextWidth(lineDescr, mDescrTextPaint);
            float columnWidth = Math.max(valueWidth, descrWidth);
            if (maxColumnPointWidth < columnWidth) maxColumnPointWidth = columnWidth;
        }


        int numberOfValue = points.size();

        float contentWidth = Math.max(maxColumnPointWidth * numberOfValue +
                mValueBetweenMargin * (numberOfValue - 1), headerWidth);


        if (currentPointer  - contentWidth / 2 - mLeftRightMargin - mPopupBorderPaint.getStrokeWidth() < 0){
            rect.left = mPopupBorderPaint.getStrokeWidth();
            rect.right = rect.left + contentWidth  + 2 * mLeftRightMargin;
        } else if (currentPointer  + contentWidth / 2 + mLeftRightMargin >
                viewPort.getWidth() - mPopupBorderPaint.getStrokeWidth()) {
            rect.right = viewPort.getWidth() - mPopupBorderPaint.getStrokeWidth();
            rect.left = rect.right - contentWidth - 2*mLeftRightMargin;
        } else {
            rect.left = currentPointer - contentWidth / 2 - mLeftRightMargin;
            rect.right = rect.left + contentWidth + 2 * mLeftRightMargin;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRoundRect(rect, 5,5, mPopupBackgroundPaint);
        canvas.drawRoundRect(rect, 5,5, mPopupBorderPaint);

        canvas.drawText(headerText, rect.left+mLeftRightMargin, headerY + topRectOffset, mPopupHeaderTextPaint);

        float x = rect.left+mLeftRightMargin;
        for (String lineDescr : mPoints.keySet()){
            mValuesTextPaint.setColor(mLineColors.get(lineDescr));
            mDescrTextPaint.setColor(mLineColors.get(lineDescr));
            String valueText = mPoints.get(lineDescr).getY().toString();

            canvas.drawText(valueText, x, valueY + topRectOffset, mValuesTextPaint);
            canvas.drawText(lineDescr, x, descrY + topRectOffset, mDescrTextPaint);

            x+=maxColumnPointWidth + mValueBetweenMargin;
        }

    }
}
