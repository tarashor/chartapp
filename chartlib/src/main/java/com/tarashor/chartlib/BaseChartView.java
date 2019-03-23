package com.tarashor.chartlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.tarashor.chartlib.data.DataPoint;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Arrays;
import java.util.Date;

public abstract class BaseChartView extends View {
    protected final static int MIN_HEIGHT_CHART_DP = 38;

    protected DateToIntChartData mData = null;

    protected Paint mNoDataTextPaint;
    protected Paint mLinesPaint;

    protected DateToIntDataLine[] dataLines;

    protected float[][] lines;
    protected int[] mLineColors;

    protected String mNoDataText = "No data!";

    protected ChartViewPort viewPort;

    protected Date xmin;
    protected Date xmax;
    protected int ymin;
    protected int ymax;

    protected ChartViewPortBuilder viewPortBuilder;
    private Bitmap bitmap;

    public BaseChartView(Context context) {
        super(context);
        init();
    }

    public BaseChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    protected void init() {
//        setWillNotDraw(false);
//        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        viewPortBuilder = new ChartViewPortBuilder();
        viewPortBuilder
                .setBottomOffsetPixels(Utils.convertDpToPixel(getContext(), 2))
                .setTopOffsetPixels(Utils.convertDpToPixel(getContext(), 2));
        viewPort = viewPortBuilder.build();

        mNoDataTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNoDataTextPaint.setColor(Color.rgb(180, 180, 180));
        mNoDataTextPaint.setTextAlign(Paint.Align.CENTER);
        mNoDataTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), 16));

        mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinesPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
    }


    public void setData(DateToIntChartData data) {
        mData = data;

        onDataChanged();

        invalidate();
    }

    protected void onDataChanged(){
        dataLines = new DateToIntDataLine[mData.getLinesCount()];
        mLineColors = new int[mData.getLinesCount()];
        for (int i = 0; i < mData.getLinesCount(); i++){
            dataLines[i] = createDataLine(mData, i);
            mLineColors[i] = mData.getColor(i);
        }

        calculateXMinAndXMax();
        ymin = 0;
        calculateYMax();

        viewPortBuilder.setXmax(xmax).setXmin(xmin)
                .setYmin(ymin).setYmax(ymax);

        setNewViewPort(viewPortBuilder.build());

    }

    protected void setNewViewPort(ChartViewPort newViewPort) {
        if (viewPort.isValid()) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = recalculateLineToDraw(lines[i], newViewPort);
            }
            viewPort = newViewPort;
        } else {
            viewPort = newViewPort;
            if (newViewPort.isValid()) {
                initAllLinesToDraw();
            }
        }

        updateBitmap();
    }

    private void updateBitmap() {
        if (bitmap != null) {
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            if (lines != null) {
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i] != null) {
                        mLinesPaint.setColor(mLineColors[i]);
                        canvas.drawLines(lines[i], mLinesPaint);
                    }
                }
            }
        }
    }


    protected void calculateYMax() {
        ymax = 0;
        if (dataLines != null && dataLines.length > 0) {
            ymax = dataLines[0].yMax;
            for (int i = 1; i < dataLines.length; i++) {
                if (dataLines[i].isVisible) {
                    int currentMax = dataLines[i].yMax;
                    if (ymax < currentMax) {
                        ymax = currentMax;
                    }
                }
            }
        }
    }

    private void calculateXMinAndXMax() {
        xmin = new Date();
        xmax = new Date();
        if (dataLines != null && dataLines.length > 0) {
            DateToIntDataLine firstDataLine = dataLines[0];
            if (firstDataLine.points != null && firstDataLine.points.length > 0) {
                xmin = firstDataLine.points[0].getX();
                xmax = firstDataLine.points[firstDataLine.points.length - 1].getX();
            }
        }
    }

    private DateToIntDataLine createDataLine(DateToIntChartData mData, int lineIndex) {
        DateToIntDataPoint[] points = new DateToIntDataPoint[mData.getXCount()];
        int yMax = mData.getY(lineIndex, 0);
        for (int j = 0; j < mData.getXCount(); j++) {
            int y = mData.getY(lineIndex, j);
            Date x = mData.getX(j);
            points[j] = new DateToIntDataPoint(x, y);
            if (yMax < y) yMax = y;
        }
        Arrays.sort(points);

        DateToIntDataLine dateToIntDataLine = new DateToIntDataLine();
        dateToIntDataLine.points = points;
        dateToIntDataLine.yMax = yMax;
        dateToIntDataLine.id = mData.getLineName(lineIndex);

        return dateToIntDataLine;
    }

    protected void initAllLinesToDraw() {
        lines = new float[dataLines.length][];
        for (int i = 0; i < dataLines.length; i++) {
            if (dataLines[i].isVisible) {
                lines[i] = convertPointsToLine(dataLines[i].points);
            } else {
                lines[i] = null;
            }
        }
    }

    protected float[] recalculateLineToDraw(float[] line, ChartViewPort chartViewPort) {
        float[] newLine = null;
        if (chartViewPort.isValid() && line != null) {
            newLine = new float[line.length];
            for (int i = 0; i < line.length; i+= 2) {
                newLine[i] = viewPort.xPixelsToOtherViewPort(line[i], chartViewPort);
                newLine[i+1] = viewPort.yPixelsToOtherViewPort(line[i+1], chartViewPort);
            }
        }
        return newLine;
    }


    protected float[] convertPointsToLine(DateToIntDataPoint[] points) {

        float[] pointsInPixes = new float[points.length * 2];

        for (int i = 0; i < points.length; i++){
            pointsInPixes[2 * i] = viewPort.xValueToPixels(points[i].getX());
            pointsInPixes[2 * i + 1] = viewPort.yValueToPixels(points[i].getY());;
        }

        pointsInPixes = Approximator.reduceWithDouglasPeucker(pointsInPixes, 2);


        float[] line = new float[(pointsInPixes.length - 2) * 2];

        for (int i = 0; i < pointsInPixes.length - 2; i+=2){
            line[2 * i] = pointsInPixes[i];
            line[2 * i + 1] = pointsInPixes[i+1];
            line[2 * i + 2] = pointsInPixes[i+2];
            line[2 * i + 3] = pointsInPixes[i+3];
        }

        return line;
    }

    public void clear() {
        mData = null;
        invalidate();

    }

    public boolean isEmpty() {
        return mData == null || mData.isEmpty();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) (Utils.convertDpToPixel(getContext(), MIN_HEIGHT_CHART_DP));
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                Math.max(getSuggestedMinimumHeight(), resolveSize(size, heightMeasureSpec)));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);

        viewPortBuilder.setHeight(h);
        viewPortBuilder.setWidth(w);
        setNewViewPort(viewPortBuilder.build());

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isEmpty()) {
            boolean hasText = !TextUtils.isEmpty(mNoDataText);
            if (hasText) {
                PointF c = viewPort.getCenter();
                canvas.drawText(mNoDataText, c.x, c.y, mNoDataTextPaint);
            }
        } else {
            canvas.drawBitmap(bitmap, 0, 0, null);
            drawView(canvas);
        }
    }

    protected abstract void drawView(Canvas canvas);



    public void setVisibilityForLine(String lineName, boolean isVisible){
        int index = findDataLineIndexByName(lineName);
        if (index > 0) {
            DateToIntDataLine dataLine = dataLines[index];
            if (dataLine != null) {
                if (dataLine.isVisible != isVisible) {
                    dataLine.isVisible = isVisible;
                    int dataLineYMax = dataLine.getYMaxInRange(viewPort.getXmin(), viewPort.getXmax());
                    if (dataLineYMax < viewPort.getYmax()) {
                        lines[index] = convertPointsToLine(dataLine.points);
                    } else {
                        viewPort = viewPortBuilder.setYmax(dataLineYMax).build();
                        initAllLinesToDraw();
                    }
                    invalidate();
                }
            }
        }
    }

    private int findDataLineIndexByName(String lineName) {
        for (int i = 0; i < dataLines.length; i++){
            if (dataLines[i].id.equals(lineName)) {
                return i;
            }
        }
        return -1;
    }


    static class DateToIntDataLine{
        String id;
        DateToIntDataPoint[] points;
        boolean isVisible = true;
        int yMax;

        public int getYMaxInRange(Date start, Date end) {
            int index = Arrays.binarySearch(points, new DateToIntDataPoint(start, 0));
            if (index < 0) index = -index - 1;
            int ymax = points[index].getY();
            index++;
            while (points[index].getX().before(end) || index < points.length){
                if (ymax < points[index].getY()){
                    ymax = points[index].getY();
                }
            }
            return ymax;
        }
    }

    static class DateToIntDataPoint extends DataPoint<Date, Integer> {
        public DateToIntDataPoint(Date x, Integer y) {
            super(x, y);
        }
    }

}