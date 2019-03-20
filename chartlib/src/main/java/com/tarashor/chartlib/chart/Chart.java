package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.tarashor.chartlib.IValueConverter;
import com.tarashor.chartlib.Utils;
import com.tarashor.chartlib.data.DataPoint;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Arrays;
import java.util.Date;


public class Chart extends View {
    protected final static int AXIS_TEXT_SIZE_DP = 16;
    protected final static int AXIS_TEXT_AREA_HEIGHT_DP = AXIS_TEXT_SIZE_DP + 4;
    protected final static int MIN_HEIGHT_CHART_DP = 38;

    protected DateToIntChartData mData = null;
    private Date xmin;
    private Date xmax;
    private int ymin;
    private int ymax;

    protected Paint mYTextPaint;
    protected Paint mXTextPaint;
    protected Paint mNoDataTextPaint;

    protected Paint mGridPaint;
    private Paint[] mLinePaints;

    private String mNoDataText = "No chart data available.";

    private float mBottomOffsetPixels = 0.f;
    private float mTopOffsetPixels = 0.f;

    private float[][] lines;

    private int topRealOffset;

    private YAxis<Integer> yAxis;
    private XAxis<Date> xAxis;

    private IValueConverter<Integer> yConverter;
    private IValueConverter<Date> xConverter;


    public Chart(Context context) {
        super(context);
        init();
    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Chart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    protected void init() {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mBottomOffsetPixels = Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP);
        mTopOffsetPixels = Utils.convertDpToPixel(getContext(), AXIS_TEXT_AREA_HEIGHT_DP);

        mYTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYTextPaint.setColor(Color.rgb(150, 162, 170));
        mYTextPaint.setTextAlign(Paint.Align.LEFT);
        mYTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mXTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXTextPaint.setColor(Color.rgb(150, 162, 170));
        mXTextPaint.setTextAlign(Paint.Align.LEFT);
        mXTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mNoDataTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNoDataTextPaint.setColor(Color.rgb(150, 162, 170));
        mNoDataTextPaint.setTextAlign(Paint.Align.CENTER);
        mNoDataTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
        mGridPaint.setColor(Color.rgb(241, 241, 242));
    }

    public void setData(DateToIntChartData data) {
        mData = data;

        calcMinMax();

        notifyDataSetChanged();
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
        int size = (int) (Utils.convertDpToPixel(getContext(), MIN_HEIGHT_CHART_DP) + mBottomOffsetPixels);
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(size, widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(size, heightMeasureSpec)));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        notifyDataSetChanged();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isEmpty()) {
            boolean hasText = !TextUtils.isEmpty(mNoDataText);
            if (hasText) {
                Point c = getCenter();
                canvas.drawText(mNoDataText, c.x, c.y, mNoDataTextPaint);
            }
            return;
        }

        drawYAxis(canvas);
        drawXAxis(canvas);


        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                canvas.drawLines(lines[i], mLinePaints[i]);
            }
        }

    }

    public Point getCenter() {
        return new Point(getWidth() / 2, getHeight() / 2);
    }


    protected float getChartAreaBottom() {
        return getHeight() - mBottomOffsetPixels;
    }

    protected float getChartAreaWidth() {
        return getWidth();
    }

    public void notifyDataSetChanged() {
        if (!isEmpty()) {
            xConverter = new DateValueConverter(xmin, xmax, getChartAreaWidth());
            yConverter = new IntegerValueConverter(ymin, ymax, getChartAreaBottom());

            yAxis = new YAxis<>(getChartAreaWidth(), getChartAreaBottom(), mTopOffsetPixels,
                    mGridPaint, mYTextPaint, yConverter);

            xAxis = new XAxis<>(getChartAreaWidth(), getChartAreaBottom(), mTopOffsetPixels,
                    mXTextPaint, xConverter, new Date());

            lines = new float[mData.getLinesCount()][];
            mLinePaints = new Paint[mData.getLinesCount()];
            for (int i = 0; i < mData.getLinesCount(); i++){
                lines[i] = convertPointsToLine(mData, i);

                mLinePaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                mLinePaints[i] .setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
                mLinePaints[i] .setColor(mData.getColor(i));
            }
        }

        invalidate();
    }



    protected void calcMinMax() {
        xmin = mData.getXMin();
        xmax = mData.getXMax();
        ymin = 0;//mData.convertYtoFloat(mData.getYMin(0));
        ymax = mData.getYMax(0);
        for (int i = 1; i < mData.getLinesCount(); i++) {
            int currentMax = mData.getYMax(i);
            if (ymax < currentMax){
                ymax = currentMax;
            }
        }
        ymax = getRealTop(ymax);

    }

    private int getRealTop(int yMax) {
        int div = 10;
        int preDiv = 1;

        while (yMax % div <=  (yMax / div * div) * mTopOffsetPixels / (getChartAreaBottom() - mTopOffsetPixels)) {
            preDiv = div;
            div *= 10;
        }

        int maxHorizontalLine = yMax / preDiv * preDiv;
        if (preDiv == 1) maxHorizontalLine = (yMax / 10 + 1)* 10;

        topRealOffset = Math.round(maxHorizontalLine * mTopOffsetPixels / (getChartAreaBottom() - mTopOffsetPixels));

        return maxHorizontalLine + topRealOffset;

    }

    private float[] convertPointsToLine(DateToIntChartData mData, int lineIndex) {
        DataPoint<Date, Integer>[] points = new DataPoint[mData.getXCount()];
        for (int j = 0; j < mData.getXCount(); j++) {
            points[j] = new DataPoint<>(mData.getX(j), mData.getY(lineIndex, j));
        }
        return convertPointsToLine(points);
    }


    private float[] convertPointsToLine(DataPoint<Date, Integer>[] points) {
        Arrays.sort(points);

        float[] line = new float[(points.length - 1) * 2 * 2];

        for (int i = 0; i < points.length - 1; i++){
            line[4 * i] = xConverter.valueToPixels(points[i].getX());
            line[4 * i + 1] = yConverter.valueToPixels(points[i].getY());
            line[4 * i + 2] = xConverter.valueToPixels(points[i+1].getX());
            line[4 * i + 3] = yConverter.valueToPixels(points[i+1].getY());;
        }

        return line;
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
