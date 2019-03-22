package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.tarashor.chartlib.BaseChartView;
import com.tarashor.chartlib.ChartViewPort;
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

        xAxis = new XAxis(mXTextPaint, new DateValueFormatter(), this);
    }


    @Override
    protected void drawView(Canvas canvas) {
        drawYAxis(canvas);
        drawXAxis(canvas);
    }


    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (!isEmpty()) {
            initYAxis();
        }
    }

    @Override
    protected void setNewViewPort(ChartViewPort newViewPort) {
        if (newViewPort.isValid()) {
            int newYMax = getRealTop(newViewPort.getYmax(), newViewPort);
            viewPortBuilder
                    .setXmin(newViewPort.getXmin())
                    .setXmax(newViewPort.getXmax())
                    .setYmin(newViewPort.getYmin())
                    .setYmax(newYMax)
                    .setBottomOffsetPixels(newViewPort.getBottomOffsetPixels())
                    .setTopOffsetPixels(newViewPort.getTopOffsetPixels())
                    .setHeight(newViewPort.getHeight())
                    .setWidth(newViewPort.getWidth());
        }
        super.setNewViewPort(viewPortBuilder.build());

        xAxis.viewPortChanged(newViewPort, xmin, xmax);
    }


    private void initYAxis() {
        yAxis = new YAxis(viewPort, mTopLineOffsetPixels, mGridPaint, mYTextPaint, new IntegerValueFormatter());
    }


    private int getRealTop(int yMax, ChartViewPort newViewPort) {
        int div = 10;
        int preDiv = 1;

        while (yMax % div <=  (yMax / div * div) * mTopLineOffsetPixels / (newViewPort.getHeight() - newViewPort.getBottomOffsetPixels() - mTopLineOffsetPixels)) {
            preDiv = div;
            div *= 10;
        }

        int maxHorizontalLine = yMax / preDiv * preDiv;
        if (preDiv == 1) maxHorizontalLine = (yMax / 10 + 1)* 10;

        int topRealOffset = Math.round(maxHorizontalLine * mTopLineOffsetPixels / (newViewPort.getHeight() - newViewPort.getBottomOffsetPixels() - mTopLineOffsetPixels));

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
        setNewViewPort(viewPortBuilder.build());

        invalidate();
    }
}
