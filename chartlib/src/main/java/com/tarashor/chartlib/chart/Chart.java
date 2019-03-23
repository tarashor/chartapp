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

    private int mGridColor;
    private int mMarksTextColor;
    private int mPointerLineColor;
    private int mPopupTextHeaderColor;
    private int mPopupBackground;
    private int mPopupBorderColor;

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
        yAxis = new YAxis(mTopLineOffsetPixels, mGridPaint, mYTextPaint, new IntegerValueFormatter());
    }

    public void setColorsForPaints(
            int gridColor,
            int marksTextColor,
            int pointerLineColor,
            int popupTextHeaderColor,
            int popupBackground,
            int popupBorderColor){
        mYTextPaint.setColor(marksTextColor);
        mXTextPaint.setColor(marksTextColor);
        mGridPaint.setColor(gridColor);
    }

    @Override
    protected void drawUnderView(Canvas canvas) {
        super.drawUnderView(canvas);
        drawYAxis(canvas);
        drawXAxis(canvas);
    }

    @Override
    protected void drawOverView(Canvas canvas) {

    }

    @Override
    protected void setNewViewPort(ChartViewPort newViewPort) {
        super.setNewViewPort(newViewPort);
        xAxis.viewPortChanged(viewPort, xmin, xmax);
        yAxis.viewPortChanged(viewPort);
    }

    @Override
    protected void setNewXForViewPort(Date start, Date end) {
        super.setNewXForViewPort(start, end);

    }

    @Override
    protected void setNewYmaxForViewPort(int yMax) {
        yMax = getRealTop(yMax, viewPortBuilder.setYmax(yMax).build());
        super.setNewYmaxForViewPort(yMax);

    }

    private int getRealTop(int yMax, ChartViewPort newViewPort) {
        if (newViewPort != null) {
            if (yMax == 0) return 0;
            if (!newViewPort.isValid()) return yMax;

            int div = 10;
            int preDiv = 1;

            while (yMax % div <= (yMax / div * div) * mTopLineOffsetPixels / (newViewPort.getHeight() - newViewPort.getBottomOffsetPixels() - mTopLineOffsetPixels)) {
                preDiv = div;
                div *= 10;
            }

            int maxHorizontalLine = yMax / preDiv * preDiv;
            if (preDiv == 1) maxHorizontalLine = (yMax / 10 + 1) * 10;

            int topRealOffset = Math.round(maxHorizontalLine * mTopLineOffsetPixels / (newViewPort.getHeight() - newViewPort.getBottomOffsetPixels() - mTopLineOffsetPixels));

            return maxHorizontalLine + topRealOffset;
        }

        return 0;

    }


    protected void drawXAxis(Canvas canvas) {
        if (xAxis != null)
            xAxis.draw(canvas);
    }

    protected void drawYAxis(Canvas canvas) {
        if (yAxis != null)
            yAxis.draw(canvas);
    }

}
