package com.tarashor.chartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tarashor.chartlib.chart.DateValueConverter;
import com.tarashor.chartlib.chart.IntegerValueConverter;
import com.tarashor.chartlib.data.DataPoint;
import com.tarashor.chartlib.data.DateToIntChartData;

import java.util.Arrays;
import java.util.Date;

public class ChartRangeSelector extends View {
    protected final static int MIN_HEIGHT_CHART_DP = 38;

    private Date start;
    private Date end;

    private final RectF leftNotFilledRect = new RectF();
    private final RectF rightNotFilledRect = new RectF();

    private final RectF leftRect = new RectF();
    private final RectF rightRect = new RectF();
    private final RectF topRect = new RectF();
    private final RectF bottomRect = new RectF();


    protected DateToIntChartData mData = null;
    private Date xmin;
    private Date xmax;
    private int ymin;
    private int ymax;

    protected Paint mNotSelectedPaint;
    protected Paint mSelectedBorder;
    protected Paint mNoDataTextPaint;

    protected Paint mGridPaint;
    private Paint[] mLinePaints;

    private float mBottomOffsetPixels = 0.f;
    private float mTopOffsetPixels = 0.f;

    private float mPortLeftRightThicknessPixels = 0.f;
    private float mPortTopBottomThicknessPixels = 0.f;
    private float mMinPortWidthPixels = 0.f;

    private float[][] lines;

    private IValueConverter<Integer> yConverter;
    private IValueConverter<Date> xConverter;
    private String mNoDataText = "No data!";

    private float leftPixels;
    private float rightPixels;


    public ChartRangeSelector(Context context) {
        super(context);
        init();
    }

    public ChartRangeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartRangeSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    protected void init() {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mBottomOffsetPixels = Utils.convertDpToPixel(getContext(), 2);
        mTopOffsetPixels = Utils.convertDpToPixel(getContext(), 2);

        mPortLeftRightThicknessPixels = Utils.convertDpToPixel(getContext(), 30);
        mPortTopBottomThicknessPixels = Utils.convertDpToPixel(getContext(), 4);
        mMinPortWidthPixels = 2*mPortLeftRightThicknessPixels;

        mNotSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNotSelectedPaint.setColor(Color.argb(51, 180, 180, 180));
        mNotSelectedPaint.setStyle(Paint.Style.FILL);

        mSelectedBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedBorder.setColor(Color.argb(125, 180, 180, 180));

        mNoDataTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNoDataTextPaint.setColor(Color.rgb(180, 180, 180));
        mNoDataTextPaint.setTextAlign(Paint.Align.CENTER);
        mNoDataTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), 16));
    }

    private DragMode dragMode = DragMode.NONE;
    private float mPreviousX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (leftRect.left <= x && x <= leftRect.right) {
                    dragMode = DragMode.LEFT;
                    setPressed(true);
                } else if (rightRect.left <= x && x <= rightRect.right) {
                    dragMode = DragMode.RIGHT;
                    setPressed(true);
                } else if (leftRect.right <= x && x <= rightRect.left) {
                    dragMode = DragMode.WHOLE;
                    setPressed(true);
                } else dragMode = DragMode.NONE;

                mPreviousX = x;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_OUTSIDE:
                float dx = x - mPreviousX;
                if (dx != 0) {
                    moveBy(dx, dragMode);
                }
                mPreviousX = x;
                break;


            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPreviousX = 0;
                dragMode = DragMode.NONE;
                setPressed(false);
                invalidate();
                break;
        }
        return true;

    }

    private void moveBy(float dx, DragMode dragMode) {
        switch (dragMode){
            case LEFT:
                setLeftPixels(leftPixels + dx);
                break;
            case RIGHT:
                setRightPixels(rightPixels + dx);
                break;
            case WHOLE:
                setLeftAndRightRight(leftPixels + dx, rightPixels + dx);
                break;
        }

        if (dragMode != DragMode.NONE){
            updateRects();
            invalidate();
        }

    }

    private void setLeftPixels(float newLeftPixels) {
        if (0 <= newLeftPixels && newLeftPixels <= rightPixels - mMinPortWidthPixels){
            leftPixels = newLeftPixels;
        }
    }

    private void setRightPixels(float newRightPixels) {
        if (leftPixels + mMinPortWidthPixels <= newRightPixels && newRightPixels <= getChartAreaWidth()){
            rightPixels = newRightPixels;
        }
    }

    private void setLeftAndRightRight(float newLeftPixels, float newRightPixels) {
        if ((0 <= newLeftPixels && newLeftPixels <= rightPixels - mMinPortWidthPixels) &&
                (leftPixels + mMinPortWidthPixels <= newRightPixels && newRightPixels <= getChartAreaWidth())) {
            leftPixels = newLeftPixels;
            rightPixels = newRightPixels;
        }
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
        int size = (int) (Utils.convertDpToPixel(getContext(), MIN_HEIGHT_CHART_DP));
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                Math.max(getSuggestedMinimumHeight(), resolveSize(size, heightMeasureSpec)));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        notifyDataSetChanged();

        super.onSizeChanged(w, h, oldw, oldh);

        leftNotFilledRect.right = leftPixels;
        leftNotFilledRect.left = 0;
        leftNotFilledRect.top = 0;
        leftNotFilledRect.bottom = h;

        rightNotFilledRect.left = rightPixels;
        rightNotFilledRect.right = w;
        rightNotFilledRect.top = 0;
        rightNotFilledRect.bottom = h;

        leftRect.left = leftNotFilledRect.right;
        leftRect.right = leftRect.left + mPortLeftRightThicknessPixels;
        leftRect.top = 0;
        leftRect.bottom = h;

        rightRect.right = rightNotFilledRect.left;
        rightRect.left = rightRect.right - mPortLeftRightThicknessPixels;
        rightRect.top = 0;
        rightRect.bottom = h;

        topRect.left = leftRect.right;
        topRect.right = rightRect.left;
        topRect.top = 0;
        topRect.bottom = mPortTopBottomThicknessPixels;

        bottomRect.left = leftRect.right;
        bottomRect.right = rightRect.left;
        bottomRect.top = h - mPortTopBottomThicknessPixels;
        bottomRect.bottom = h;
    }

    private void updateRects() {
        leftNotFilledRect.right = leftPixels;
        rightNotFilledRect.left = rightPixels;

        leftRect.left = leftNotFilledRect.right;
        leftRect.right = leftRect.left + mPortLeftRightThicknessPixels;

        rightRect.right = rightNotFilledRect.left;
        rightRect.left = rightRect.right - mPortLeftRightThicknessPixels;

        topRect.left = leftRect.right;
        topRect.right = rightRect.left;

        bottomRect.left = leftRect.right;
        bottomRect.right = rightRect.left;
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

        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                canvas.drawLines(lines[i], mLinePaints[i]);
            }
        }


        canvas.drawRect(leftNotFilledRect, mNotSelectedPaint);
        canvas.drawRect(rightNotFilledRect, mNotSelectedPaint);
        canvas.drawRect(leftRect, mSelectedBorder);
        canvas.drawRect(rightRect, mSelectedBorder);
        canvas.drawRect(topRect, mSelectedBorder);
        canvas.drawRect(bottomRect, mSelectedBorder);



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

            lines = new float[mData.getLinesCount()][];
            mLinePaints = new Paint[mData.getLinesCount()];
            for (int i = 0; i < mData.getLinesCount(); i++){
                lines[i] = convertPointsToLine(mData, i);

                mLinePaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                mLinePaints[i].setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
                mLinePaints[i].setColor(mData.getColor(i));
            }

            setStartInternal(xmin);
            setEndInternal(xmax);
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


    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void setStart(Date start) {
        setStartInternal(start);
        invalidate();
    }

    public void setEnd(Date end) {
        setEndInternal(start);
        invalidate();
    }

    private void setStartInternal(Date start) {
        this.start = start;
        leftPixels = xConverter.valueToPixels(this.start);
        updateRects();

    }

    private void setEndInternal(Date end) {
        this.end = end;
        rightPixels = xConverter.valueToPixels(this.end);
        updateRects();
    }
}
