package com.tarashor.chartlib.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.tarashor.chartlib.IValueConverter;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


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
    private Matrix transformToScreenMatrix;
    private Matrix transformToRealMatrix;


    private int topRealOffset;

    private YAxis<Integer> yAxis;
    private XAxis<Date> xAxis;

    private IValueConverter<Integer> yConverter = new IValueConverter<Integer>() {
        @Override
        public String format(Integer v) {
            return String.valueOf(v);
        }

        @Override
        public float valueToPixels(Integer v) {
            return convertIntegerToFloat(v) * getChartAreaBottom() / (convertIntegerToFloat(ymax) - convertIntegerToFloat(ymin));
        }

        @Override
        public Integer pixelsToValue(float pixels) {
            return Math.round(convertIntegerToFloat(ymax) - pixels * (convertIntegerToFloat(ymax) - convertIntegerToFloat(ymin)) / (getChartAreaBottom()));
        }
    };

    private IValueConverter<Date> xConverter = new IValueConverter<Date>() {
        @Override
        public String format(Date v) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.ENGLISH);
            return sdf.format(v);

        }

        @Override
        public float valueToPixels(Date v) {
            return convertDateToFloat(v) * getChartAreaWidth() / (convertDateToFloat(xmax) - convertDateToFloat(xmin));
        }

        @Override
        public Date pixelsToValue(float pixels) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis((long)(convertDateToFloat(xmin) + pixels * (convertDateToFloat(xmax) - convertDateToFloat(xmin)) / (getChartAreaWidth())));
            return c.getTime();
        }
    };

    private float convertIntegerToFloat(Integer v){
        return v;
    }

    private float convertDateToFloat(Date v){
        return v.getTime();
    }

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

        calculateTransformMatrix();

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
            lines = new float[mData.getLinesCount()][];
            mLinePaints = new Paint[mData.getLinesCount()];
            for (int i = 0; i < mData.getLinesCount(); i++){
                PointF[] points = new PointF[mData.getXCount()];
                for (int j = 0; j < mData.getXCount(); j++) {
                    points[j] = new PointF(convertDateToFloat(mData.getX(j)), convertIntegerToFloat(mData.getY(i, j)));
                }
                lines[i] = convertPointsToLine(points);
                mLinePaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                mLinePaints[i] .setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
                mLinePaints[i] .setColor(mData.getColor(i));
            }

            yAxis = new YAxis<>(getChartAreaWidth(), getChartAreaBottom(), mTopOffsetPixels,
                    mGridPaint, mYTextPaint, yConverter);

            xAxis = new XAxis<>(getChartAreaWidth(), getChartAreaBottom(), mTopOffsetPixels,
                    mXTextPaint, xConverter, new Date());
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


    private float[] convertPointsToLine(PointF[] points) {
        Arrays.sort(points, new Comparator<PointF>() {
            @Override
            public int compare(PointF o1, PointF o2) {
                return Float.compare(o1.x, o2.x);
            }
        });

        float[] line = new float[(points.length - 1) * 2 * 2];

        for (int i = 0; i < points.length - 1; i++){
            PointF start = convertToLocalCoordinates(points[i]);
            PointF end = convertToLocalCoordinates(points[i + 1]);
            line[4 * i] = start.x;
            line[4 * i + 1] = start.y;
            line[4 * i + 2] = end.x;
            line[4 * i + 3] = end.y;
        }

        return line;
    }


    protected void calculateTransformMatrix() {
        transformToScreenMatrix = new Matrix();
        transformToScreenMatrix.setTranslate(-convertDateToFloat(xmin), -convertIntegerToFloat(ymin));
        float sx = getChartAreaWidth() / (convertDateToFloat(xmax) - convertDateToFloat(xmin));
        float sy = getChartAreaBottom() / (convertIntegerToFloat(ymax) - convertIntegerToFloat(ymin));
        transformToScreenMatrix.postScale(sx, -sy);
        transformToScreenMatrix.postTranslate(0, getChartAreaBottom());

    }

    private PointF convertToLocalCoordinates(PointF point) {
        float[] screenPoint = new float[2];
        transformToScreenMatrix.mapPoints(screenPoint, new float[]{point.x, point.y});
        return new PointF(screenPoint[0], screenPoint[1]);
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
