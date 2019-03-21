package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.tarashor.chartlib.BaseChartView;
import com.tarashor.chartlib.IValueFormatter;
import com.tarashor.chartlib.Utils;

import java.util.Date;


public class Chart extends BaseChartView {
    protected final static int AXIS_TEXT_SIZE_DP = 16;
    protected final static int AXIS_TEXT_AREA_HEIGHT_DP = AXIS_TEXT_SIZE_DP + 4;

    protected Paint mYTextPaint;
    protected Paint mXTextPaint;

    protected Paint mGridPaint;

    private YAxis yAxis;
    private XAxis xAxis;

    private float mTopLineOffsetPixels;


    public Chart(Context context) {
        super(context);

    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Chart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    protected void init() {
        super.init();

        viewPortBuilder.setBottomOffsetPixels(Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP));
        viewPortBuilder.setTopOffsetPixels(Utils.convertDpToPixel(getContext(), 2));

        mTopLineOffsetPixels = Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP);

        mYTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYTextPaint.setColor(Color.rgb(150, 162, 170));
        mYTextPaint.setTextAlign(Paint.Align.LEFT);
        mYTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mXTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXTextPaint.setColor(Color.rgb(150, 162, 170));
        mXTextPaint.setTextAlign(Paint.Align.LEFT);
        mXTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
        mGridPaint.setColor(Color.rgb(241, 241, 242));
    }


    @Override
    protected void drawView(Canvas canvas) {
        drawYAxis(canvas);
        drawXAxis(canvas);

//
//        if (lines != null) {
//            for (int i = 0; i < lines.length; i++) {
//                if (lines[i] != null) {
//                    mLinesPaint.setColor(mLineColors[i]);
//                    canvas.drawLines(lines[i], mLinesPaint);
//                }
//            }
//        }
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (!isEmpty()) {
            ymax = getRealTop(ymax);
            viewPortBuilder.setYmax(ymax);
            viewPort = viewPortBuilder.build();
            recalculateAllLinesToDraw();

            initXAxis();
            initYAxis();
        }
    }

    private void initXAxis() {
        xAxis = new XAxis(viewPort, mXTextPaint, new DateValueFormatter());
    }

    private void initYAxis() {
        yAxis = new YAxis(viewPort, mGridPaint, mYTextPaint, new IntegerValueFormatter());
    }


    private int getRealTop(int yMax) {
        int div = 10;
        int preDiv = 1;

        while (yMax % div <=  (yMax / div * div) * mTopLineOffsetPixels / (viewPort.getHeight() - viewPort.getBottomOffsetPixels() - mTopLineOffsetPixels)) {
            preDiv = div;
            div *= 10;
        }

        int maxHorizontalLine = yMax / preDiv * preDiv;
        if (preDiv == 1) maxHorizontalLine = (yMax / 10 + 1)* 10;

        int topRealOffset = Math.round(maxHorizontalLine * mTopLineOffsetPixels / (viewPort.getHeight() - viewPort.getBottomOffsetPixels() - mTopLineOffsetPixels));

        return maxHorizontalLine + topRealOffset;

    }


    protected void drawXAxis(Canvas canvas) {
        if (xAxis != null)
            xAxis.draw(canvas);
    }

    protected void drawYAxis(Canvas canvas) {
        if (yAxis != null)
            yAxis.draw(canvas);
    }

    public void setXRange(Date start, Date end) {
        viewPortBuilder.setXmin(start);
        viewPortBuilder.setXmax(end);
        viewPort = viewPortBuilder.build();
        if (!isEmpty()) {
            xAxis.viewPortChanged(viewPort);
            recalculateAllLinesToDraw();
        }

        invalidate();
    }
}
