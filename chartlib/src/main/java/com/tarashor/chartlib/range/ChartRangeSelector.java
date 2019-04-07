package com.tarashor.chartlib.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.tarashor.chartlib.Approximator;
import com.tarashor.chartlib.BaseChartView;
import com.tarashor.chartlib.ChartViewPort;
import com.tarashor.chartlib.Utils;

import java.util.Date;

public class ChartRangeSelector extends BaseChartView {

    private final RectF leftNotFilledRect = new RectF();
    private final RectF rightNotFilledRect = new RectF();

    private final RectF leftRect = new RectF();
    private final RectF rightRect = new RectF();
    private final RectF topRect = new RectF();
    private final RectF bottomRect = new RectF();

    protected Paint mNotSelectedPaint;
    protected Paint mSelectedBorder;

    private float mPortLeftRightThicknessPixels;
    private float mPortTopBottomThicknessPixels;
    private float mMinPortWidthPixels;

    public Date start;
    public Date end;
    private float leftPixels;
    private float rightPixels;

    //private Bitmap bitmap;
    private GestureDetector mDetector;
    private DragMode mDragMode = DragMode.NONE;

    private OnRangeChangedListener listener;


    public ChartRangeSelector(Context context) {
        super(context);
    }

    public ChartRangeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartRangeSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    protected void init() {
        super.init();

        mPortLeftRightThicknessPixels = Utils.convertDpToPixel(getContext(), 15);
        mPortTopBottomThicknessPixels = Utils.convertDpToPixel(getContext(), 4);
        mMinPortWidthPixels = 2*mPortLeftRightThicknessPixels;

        mNotSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNotSelectedPaint.setColor(Color.argb(51, 180, 180, 180));
        mNotSelectedPaint.setStyle(Paint.Style.FILL);

        mSelectedBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedBorder.setColor(Color.argb(125, 180, 180, 180));


        mDetector = new GestureDetector(getContext(),new SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                moveBy(-distanceX, mDragMode);
                return true;//super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                if (leftRect.left - mMinPortWidthPixels/2 <= x && x <= leftRect.right + mMinPortWidthPixels/2) {
                    mDragMode = DragMode.LEFT;
                    return true;
                } else if (rightRect.left - mMinPortWidthPixels/2 <= x && x <= rightRect.right + mMinPortWidthPixels/2) {
                    mDragMode = DragMode.RIGHT;
                    return true;
                } else if (leftRect.right <= x && x <= rightRect.left) {
                    mDragMode = DragMode.WHOLE;
                    return true;
                } else {
                    mDragMode = DragMode.NONE;
                    return super.onDown(event);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector.onTouchEvent(event))
            return true;
        else {
            switch (event.getAction()){
                case MotionEvent.ACTION_UP:
                    getParent().requestDisallowInterceptTouchEvent(false);

                    break;
            }
        }
        return super.onTouchEvent(event);

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
                float dist = rightPixels - leftPixels;
                if (leftPixels + dx < 0) {
                    setLeftAndRight(0, dist);
                } else if (rightPixels + dx > viewPort.getWidth()){
                    setLeftAndRight(viewPort.getWidth() - dist, viewPort.getWidth());
                } else {
                    setLeftAndRight(leftPixels + dx, rightPixels + dx);
                }
                break;
        }

        if (dragMode != DragMode.NONE){
            invalidate();
        }
    }

    @Override
    protected void setNewViewPort(ChartViewPort newViewPort) {
        super.setNewViewPort(newViewPort);

        leftPixels = viewPort.xValueToPixels(start);
        rightPixels = viewPort.xValueToPixels(end);

        float width = viewPort.getWidth();
        float height = viewPort.getHeight();

        leftNotFilledRect.right = leftPixels;
        leftNotFilledRect.left = 0;
        leftNotFilledRect.top = 0;
        leftNotFilledRect.bottom = height;

        rightNotFilledRect.left = rightPixels;
        rightNotFilledRect.right = width;
        rightNotFilledRect.top = 0;
        rightNotFilledRect.bottom = height;

        leftRect.left = leftNotFilledRect.right;
        leftRect.right = leftRect.left + mPortLeftRightThicknessPixels;
        leftRect.top = 0;
        leftRect.bottom = height;

        rightRect.right = rightNotFilledRect.left;
        rightRect.left = rightRect.right - mPortLeftRightThicknessPixels;
        rightRect.top = 0;
        rightRect.bottom = height;

        topRect.left = leftRect.right;
        topRect.right = rightRect.left;
        topRect.top = 0;
        topRect.bottom = mPortTopBottomThicknessPixels;

        bottomRect.left = leftRect.right;
        bottomRect.right = rightRect.left;
        bottomRect.top = height - mPortTopBottomThicknessPixels;
        bottomRect.bottom = height;
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
    protected void drawOverView(Canvas canvas) {
        canvas.drawRect(leftNotFilledRect, mNotSelectedPaint);
        canvas.drawRect(rightNotFilledRect, mNotSelectedPaint);
        canvas.drawRect(leftRect, mSelectedBorder);
        canvas.drawRect(rightRect, mSelectedBorder);
        canvas.drawRect(topRect, mSelectedBorder);
        canvas.drawRect(bottomRect, mSelectedBorder);
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (!isEmpty()) {
            start = xmin;
            end = xmax;
        }
    }

    @Override
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
        float newLeftPixels = viewPort.xValueToPixels(start);
        setLeftPixels(newLeftPixels);
    }

    private void setEndInternal(Date end) {
        float newRightPixels = viewPort.xValueToPixels(end);
        setRightPixels(newRightPixels);
    }



    private void setLeftPixels(float newLeftPixels) {
        if (newLeftPixels < 0) newLeftPixels = 0;
        else {
            float rightEdge = rightPixels - mPortLeftRightThicknessPixels - mMinPortWidthPixels;
            if (newLeftPixels > rightEdge) newLeftPixels = rightEdge;
        }

        if (leftPixels != newLeftPixels) {
            leftPixels = newLeftPixels;
            this.start = viewPort.xPixelsToValue(leftPixels);
            updateRects();
            onRangeChanged();
        }
    }

    private void setRightPixels(float newRightPixels) {
        if (newRightPixels > viewPort.getWidth()) newRightPixels = viewPort.getWidth();
        else {
            float leftEdge = leftPixels + mPortLeftRightThicknessPixels + mMinPortWidthPixels;
            if (newRightPixels < leftEdge) newRightPixels = leftEdge;
        }
        if (rightPixels != newRightPixels) {
            rightPixels = newRightPixels;
            this.end = viewPort.xPixelsToValue(rightPixels);
            updateRects();
            onRangeChanged();
        }

    }

    private void setLeftAndRight(float newLeftPixels, float newRightPixels) {
        boolean isChanged = false;
        if (leftPixels != newLeftPixels) {
            leftPixels = newLeftPixels;
            this.start = viewPort.xPixelsToValue(leftPixels);
            isChanged = true;
        }
        if (rightPixels != newRightPixels) {
            rightPixels = newRightPixels;
            this.end = viewPort.xPixelsToValue(rightPixels);
            isChanged = true;
        }
        if (isChanged) {
            updateRects();
            onRangeChanged();
        }

    }

    private void onRangeChanged(){
        if (listener != null){
            listener.onRangeChanged(this, start, end);
        }
    }

    public void setListener(OnRangeChangedListener listener) {
        this.listener = listener;
    }



    public interface OnRangeChangedListener {

        void onRangeChanged(ChartRangeSelector v, Date start, Date end);
    }

}
