package com.tarashor.chartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public abstract class Chart<T extends ChartData> extends View {
    protected final static int AXIS_TEXT_SIZE_DP = 16;
    protected final static int AXIS_TEXT_AREA_HEIGHT_DP = AXIS_TEXT_SIZE_DP + 4;
    protected final static int MIN_HEIGHT_CHART_DP = 38;


    protected boolean mLogEnabled = false;
    protected T mData = null;

    protected Paint mTextPaint;
    protected Paint mGridPaint;

    private String mNoDataText = "No chart data available.";

    private float mBottomOffsetPixels = 0.f;


    private float mExtraTopOffset = 0.f,
            mExtraRightOffset = 0.f,
            mExtraBottomOffset = 0.f,
            mExtraLeftOffset = 0.f;


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

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.rgb(150, 162, 170));
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(Utils.convertDpToPixel(getContext(), AXIS_TEXT_SIZE_DP));

        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStrokeWidth(Utils.convertDpToPixel(getContext(), 2));
        mGridPaint.setColor(Color.rgb(241, 241, 242));

        if (mLogEnabled)
            Log.i("", "Chart.init()");
    }

    public void setData(T data) {
        mData = data;
        mOffsetsCalculated = false;

        if (data == null) {
            return;
        }

        calcMinMax();

        notifyDataSetChanged();
    }

    public void clear() {
        mData = null;
        mOffsetsCalculated = false;
        invalidate();
    }

    public boolean isEmpty() {
        return mData == null || mData.isEmpty();
    }

    public abstract void notifyDataSetChanged();

    protected abstract void calculateOffsets();

    protected abstract void calcMinMax();

    private boolean mOffsetsCalculated = false;

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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
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
                canvas.drawText(mNoDataText, c.x, c.y, mTextPaint);
            }
            return;
        }

        if (!mOffsetsCalculated) {
            calculateOffsets();
            mOffsetsCalculated = true;
        }
    }

    protected float getChartAreaHeight() {
        return getHeight() - mBottomOffsetPixels;
    }

    protected float getChartAreaWidth() {
        return getWidth();
    }

    public Point getCenter() {
        return new Point(getWidth() / 2, getHeight() / 2);
    }



    public void setExtraOffsets(float left, float top, float right, float bottom) {
        setExtraLeftOffset(left);
        setExtraTopOffset(top);
        setExtraRightOffset(right);
        setExtraBottomOffset(bottom);
    }


    public void setExtraTopOffset(float offset) {
        mExtraTopOffset = Utils.convertDpToPixel(getContext(), offset);
    }


    public float getExtraTopOffset() {
        return mExtraTopOffset;
    }


    public void setExtraRightOffset(float offset) {
        mExtraRightOffset = Utils.convertDpToPixel(getContext(), offset);
    }

    public float getExtraRightOffset() {
        return mExtraRightOffset;
    }


    public void setExtraBottomOffset(float offset) {
        mExtraBottomOffset = Utils.convertDpToPixel(getContext(), offset);
    }

    public float getExtraBottomOffset() {
        return mExtraBottomOffset;
    }

    public void setExtraLeftOffset(float offset) {
        mExtraLeftOffset = Utils.convertDpToPixel(getContext(), offset);
    }

    public float getExtraLeftOffset() {
        return mExtraLeftOffset;
    }

    public void setLogEnabled(boolean enabled) {
        mLogEnabled = enabled;
    }

    public boolean isLogEnabled() {
        return mLogEnabled;
    }

    public void setNoDataText(String text) {
        mNoDataText = text;
    }

    public void setNoDataTextColor(int color) {
        mTextPaint.setColor(color);
    }

    public void setNoDataTextTypeface(Typeface tf) {
        mTextPaint.setTypeface(tf);
    }

    public T getData() {
        return mData;
    }



    public void setHardwareAccelerationEnabled(boolean enabled) {

        if (enabled)
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mUnbind)
            unbindDrawables(this);
    }

    private boolean mUnbind = false;

    /**
     * Unbind all drawables to avoid memory leaks.
     * Link: http://stackoverflow.com/a/6779164/1590502
     *
     * @param view
     */
    private void unbindDrawables(View view) {

        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * Set this to true to enable "unbinding" of drawables. When a View is detached
     * from a window. This helps avoid memory leaks.
     * Default: false
     * Link: http://stackoverflow.com/a/6779164/1590502
     *
     * @param enabled
     */
    public void setUnbindEnabled(boolean enabled) {
        this.mUnbind = enabled;
    }
}
