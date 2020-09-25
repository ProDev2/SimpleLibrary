package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

import com.simplelib.R;

public class BoundLinearLayout extends LinearLayout {
    public static final int UNDEFINED_SIZE = -1;

    public static final int MODE_NONE = 0;
    public static final int MODE_WRAP = 1;
    public static final int MODE_MATCH = 2;

    private boolean forceMin;

    private int minWidth, minHeight;
    private int maxWidth, maxHeight;

    private int widthMode, heightMode;

    public BoundLinearLayout(@NonNull Context context) {
        super(context);
        initialize(null, 0, 0);
    }

    public BoundLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0, 0);
    }

    public BoundLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BoundLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        //Use defaults
        forceMin = false;

        minWidth = UNDEFINED_SIZE;
        minHeight = UNDEFINED_SIZE;

        maxWidth = UNDEFINED_SIZE;
        maxHeight = UNDEFINED_SIZE;

        widthMode = MODE_NONE;
        heightMode = MODE_NONE;

        //Fetch styled attributes
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BoundLinearLayout, defStyleAttr, defStyleRes);

            forceMin = array.getBoolean(R.styleable.BoundLinearLayout_bll_force_min, forceMin);

            minWidth = array.getDimensionPixelSize(R.styleable.BoundLinearLayout_bll_minWidth, minWidth);
            minHeight = array.getDimensionPixelSize(R.styleable.BoundLinearLayout_bll_minHeight, minHeight);

            maxWidth = array.getDimensionPixelSize(R.styleable.BoundLinearLayout_bll_maxWidth, maxWidth);
            maxHeight = array.getDimensionPixelSize(R.styleable.BoundLinearLayout_bll_maxHeight, maxHeight);

            widthMode = array.getInt(R.styleable.BoundLinearLayout_bll_widthMode, widthMode);
            heightMode = array.getInt(R.styleable.BoundLinearLayout_bll_heightMode, heightMode);

            array.recycle();
        }
    }

    public void setMin(int minWidth, int minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        requestLayout();
    }

    public void setMax(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        requestLayout();
    }

    public void setMode(int widthMode, int heightMode) {
        this.widthMode = widthMode;
        this.heightMode = heightMode;
        requestLayout();
    }

    public void setForceMin(boolean forceMin) {
        this.forceMin = forceMin;
        requestLayout();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        int suggestedMinimumWidth = super.getSuggestedMinimumWidth();
        if (minWidth != UNDEFINED_SIZE)
            return Math.max(minWidth, suggestedMinimumWidth);
        else
            return suggestedMinimumWidth;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        int suggestedMinimumHeight = super.getSuggestedMinimumHeight();
        if (minHeight != UNDEFINED_SIZE)
            return Math.max(minHeight, suggestedMinimumHeight);
        else
            return suggestedMinimumHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Make measure spec
        widthMeasureSpec = getMeasureSpec(widthMeasureSpec, this.widthMode, this.maxWidth);
        heightMeasureSpec = getMeasureSpec(heightMeasureSpec, this.heightMode, this.maxHeight);

        //Measure layout
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Check against minimum height and width
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int minWidth = this.minWidth;
        int minHeight = this.minHeight;

        minWidth = getMinSpecSize(widthMeasureSpec, minWidth);
        minHeight = getMinSpecSize(heightMeasureSpec, minHeight);

        //Update measureSpecs if needed
        boolean remeasure = false;
        if (minWidth != UNDEFINED_SIZE && measuredWidth < minWidth) {
            widthMeasureSpec = getMeasureSpec(widthMeasureSpec, MODE_MATCH, minWidth);
            remeasure = true;
        }
        if (minHeight != UNDEFINED_SIZE && measuredHeight < minHeight) {
            heightMeasureSpec = getMeasureSpec(heightMeasureSpec, MODE_MATCH, minHeight);
            remeasure = true;
        }

        //Remeasure layout if needed
        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            measuredWidth = getMeasuredWidth();
            measuredHeight = getMeasuredHeight();
        }

        if (forceMin) {
            if (minWidth != UNDEFINED_SIZE)
                measuredWidth = Math.max(measuredWidth, minWidth);
            if (minHeight != UNDEFINED_SIZE)
                measuredHeight = Math.max(measuredHeight, minHeight);

            //Set measured dimension
            setMeasuredDimension(
                    resolveSizeAndState(measuredWidth, widthMeasureSpec, 0),
                    resolveSizeAndState(measuredHeight, heightMeasureSpec, 0)
            );
        }
    }

    private static int getMeasureSpec(int measureSpec, int mode, int size) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        final boolean noBoundary = size == UNDEFINED_SIZE;

        final int resultSize;
        final int resultMode;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                resultSize = noBoundary ? specSize : Math.min(specSize, size);

                if (mode == MODE_MATCH) {
                    resultMode = MeasureSpec.EXACTLY;
                } else if (mode == MODE_WRAP) {
                    resultMode = MeasureSpec.AT_MOST;
                } else {
                    resultMode = specMode;
                }
                break;

            default:
            case MeasureSpec.UNSPECIFIED:
                resultSize = noBoundary ? specSize : size;

                if (noBoundary) {
                    resultMode = MeasureSpec.UNSPECIFIED;
                } else if (mode == MODE_MATCH) {
                    resultMode = MeasureSpec.EXACTLY;
                } else if (mode == MODE_WRAP) {
                    resultMode = MeasureSpec.AT_MOST;
                } else {
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;
        }

        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    private static int getMinSpecSize(int measureSpec, int size) {
        if (size == UNDEFINED_SIZE)
            return UNDEFINED_SIZE;

        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        final int resultSize;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                resultSize = Math.min(specSize, size);
                break;

            default:
            case MeasureSpec.UNSPECIFIED:
                resultSize = size;
                break;
        }

        return resultSize;
    }
}
