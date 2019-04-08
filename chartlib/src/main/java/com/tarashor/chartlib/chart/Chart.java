package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tarashor.chartlib.BaseChartView;
import com.tarashor.chartlib.ChartViewPort;
import com.tarashor.chartlib.Utils;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class Chart extends BaseChartView {
    protected final static int AXIS_TEXT_SIZE_DP = 12;
    protected final static int AXIS_TEXT_AREA_HEIGHT_DP = AXIS_TEXT_SIZE_DP + 4;

    protected Paint mYTextPaint;
    protected Paint mXTextPaint;

    protected Paint mGridPaint;

    private Paint mPointerLinePaint;

    private YAxis yAxis;
    private XAxis xAxis;

    private float mTopLineOffsetPixels;

    private float mPointerCircleRadius;
    private Paint mPointerBorderPaint;

    private Paint mPopupBorderPaint;
    private Paint mPopupBackgroundPaint;
    private Paint mPopupHeaderTextPaint;
    private Paint mValuesTextPaint;
    private Paint mDescrTextPaint;

    private boolean isMovingPointer = false;


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
        viewPortBuilder.setTopOffsetPixels(Utils.convertDpToPixel(getContext(), 6));

        mPointerCircleRadius = Utils.convertDpToPixel(getContext(), 4);

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

        mPointerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerLinePaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 1.5f));
        mPointerLinePaint.setColor(Color.BLACK);

        mPointerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerBorderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerBorderPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 3));

        mPopupBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopupBorderPaint.setStyle(Paint.Style.STROKE);
        mPopupBorderPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));

        mPopupBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopupBackgroundPaint.setStyle(Paint.Style.FILL);

        mPopupHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopupHeaderTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), 18));
        mPopupHeaderTextPaint.setTextAlign(Paint.Align.LEFT);
        mPopupHeaderTextPaint.setFakeBoldText(true);

        mValuesTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuesTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), 22));
        mValuesTextPaint.setTextAlign(Paint.Align.LEFT);
        mValuesTextPaint.setFakeBoldText(true);

        mDescrTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDescrTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), 16));
        mDescrTextPaint.setTextAlign(Paint.Align.LEFT);

        xAxis = new XAxis(mXTextPaint, new DateValueFormatter(), this);
        yAxis = new YAxis(mTopLineOffsetPixels, mGridPaint, mYTextPaint, new IntegerValueFormatter());

        popup = new PointerPopup(getContext(), mPopupBackgroundPaint, mPopupBorderPaint, mPopupHeaderTextPaint, mValuesTextPaint, mDescrTextPaint);

    }

    private float currentPointerToDraw;

    private CountDownTimer timer = new CountDownTimer(100, 100) {


        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    };

    float previousX = -1;
    float previousY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                showPointerAt(x);
                invalidate();
                previousX = x;
                previousY = y;
                isMovingPointer = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isMovingPointer && Math.abs(x - previousX) > Math.abs(y - previousY)) {
                    isMovingPointer = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                showPointerAt(x);
                previousX = x;
                previousY = y;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                previousX = -1;
                previousY = -1;
                isMovingPointer = false;
                setPressed(false);
                getParent().requestDisallowInterceptTouchEvent(false);
                hidePointer();
                invalidate();
                return true;
        }

        return false;
    }

    private void hidePointer() {

    }

    private PointerPopup popup;
    private DateToIntDataPoint[] points;
    private Path[] pointsOut;

    private void showPointerAt(float x) {
        Date date = viewPort.xPixelsToValue(x);
        Date closestDate = null;
        Map<String, DateToIntDataPoint> popupValues = new HashMap<>();
        Map<String, Integer> popupColors = new HashMap<>();
        for(int i =0; i < dataLines.length; i++){
            pointsOut[i].reset();
            if (dataLines[i].isVisible){
                points[i] = dataLines[i].getClosestPoint(date);
                pointsOut[i].addCircle(viewPort.xValueToPixels(points[i].getX()),
                        viewPort.yValueToPixels(points[i].getY()),
                        mPointerCircleRadius - 1, Path.Direction.CCW);
                closestDate = points[i].getX();
                popupValues.put(dataLines[i].id, points[i]);
                popupColors.put(dataLines[i].id, mLineColors[i]);
            } else {
                points[i] = null;
            }
        }

        currentPointerToDraw = viewPort.xValueToPixels(closestDate);

        popup.setCurrentPointer(currentPointerToDraw, popupValues, popupColors, viewPort);
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
        mPointerLinePaint.setColor(pointerLineColor);

        mPopupBorderPaint.setColor(popupBorderColor);
        mPopupBackgroundPaint.setColor(popupBackground);
        mPopupHeaderTextPaint.setColor(popupTextHeaderColor);
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        points = new DateToIntDataPoint[dataLines.length];
        pointsOut = new Path[dataLines.length];
        for (int i = 0; i < dataLines.length; i++) {
            pointsOut[i] = new Path();
        }
    }

    @Override
    protected void drawUnderView(Canvas canvas) {
        if (isPressed()) {
            canvas.save();
            for (Path aPointsOut : pointsOut) {
                canvas.clipPath(aPointsOut, Region.Op.DIFFERENCE);
            }
        }
        drawYAxis(canvas);
        drawXAxis(canvas);
    }

    @Override
    protected void drawOverView(Canvas canvas) {
        if (isPressed()) {
            canvas.drawLine(currentPointerToDraw, viewPort.getHeight() - viewPort.getBottomOffsetPixels(),
                    currentPointerToDraw, viewPort.getTopOffsetPixels(), mPointerLinePaint);

            for (int i = 0; i < points.length; i++) {
                if (points[i] != null) {
                    mPointerBorderPaint.setColor(mLineColors[i]);
                    float x = viewPort.xValueToPixels(points[i].getX());
                    float y = viewPort.yValueToPixels(points[i].getY());
                    canvas.drawCircle(x, y, mPointerCircleRadius, mPointerBorderPaint);
                }
            }

            canvas.restore();
            popup.draw(canvas);
        }
    }

    @Override
    protected void setNewViewPort(ChartViewPort newViewPort) {
        int yMax = getRealTop(newViewPort.getYmax(), newViewPort);
        viewPortBuilder.setYmax(yMax);
        super.setNewViewPort(viewPortBuilder.build());
        xAxis.viewPortChanged(viewPort, xmin, xmax);
        yAxis.viewPortChangedAndCalculate(viewPort);
        setPressed(false);
    }

    private int getRealTop(int yMax, ChartViewPort newViewPort) {
        if (newViewPort != null) {
            if (yMax == 0) return 0;
            if (!newViewPort.isValid()) return yMax;

            int div = 10;
            int preDiv = 1;

            float heightFromZeroToLastHorizontalLine = newViewPort.getHeight() - newViewPort.getBottomOffsetPixels() - newViewPort.getTopOffsetPixels() - mTopLineOffsetPixels;

            while (yMax % div <= (yMax / div * div) * mTopLineOffsetPixels / (heightFromZeroToLastHorizontalLine)) {
                preDiv = div;
                div *= 10;
            }

            int lastHorizontalLineValue = yMax / preDiv * preDiv;
            if (preDiv == 1) lastHorizontalLineValue = (yMax / 10 + 1) * 10;

            int topRealOffset = Math.round(lastHorizontalLineValue * mTopLineOffsetPixels / heightFromZeroToLastHorizontalLine);

            return lastHorizontalLineValue + topRealOffset;
        }

        return 0;

    }


    protected void drawXAxis(Canvas canvas) {
        if (xAxis != null)
            xAxis.draw(canvas);
    }

    protected void drawYAxis(Canvas canvas) {
        if (yAxis != null){
            yAxis.draw(canvas, 1);
        }
    }

    @Override
    public void restore(DateToIntChartData data, Date xmin, Date xmax, DateToIntDataLine[] dataLines, Date vxmin, Date vxmax) {
        super.restore(data, xmin, xmax, dataLines, vxmin, vxmax);
        points = new DateToIntDataPoint[dataLines.length];
        pointsOut = new Path[dataLines.length];
        for (int i = 0; i < dataLines.length; i++) {
            pointsOut[i] = new Path();
        }
    }
}
