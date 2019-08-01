package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.simplelib.R;

public class BoundRelativeLayout extends RelativeLayout {
    public static final int UNDEFINED_SIZE = -1;

    public static final int MODE_NONE = 0;
    public static final int MODE_WRAP = 1;
    public static final int MODE_MATCH = 2;

    private int minWidth, minHeight;
    private int maxWidth, maxHeight;

    private int widthMode, heightMode;

    public BoundRelativeLayout(@NonNull Context context) {
        super(context);
        initialize(null, 0, 0);
    }

    public BoundRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0, 0);
    }

    public BoundRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BoundRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        //Use defaults
        minWidth = UNDEFINED_SIZE;
        minHeight = UNDEFINED_SIZE;

        maxWidth = UNDEFINED_SIZE;
        maxHeight = UNDEFINED_SIZE;

        widthMode = MODE_NONE;
        heightMode = MODE_NONE;

        //Fetch styled attributes
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BoundRelativeLayout, defStyleAttr, defStyleRes);

            minWidth = array.getDimensionPixelSize(R.styleable.BoundRelativeLayout_brl_minWidth, minWidth);
            minHeight = array.getDimensionPixelSize(R.styleable.BoundRelativeLayout_brl_minHeight, minHeight);

            maxWidth = array.getDimensionPixelSize(R.styleable.BoundRelativeLayout_brl_maxWidth, maxWidth);
            maxHeight = array.getDimensionPixelSize(R.styleable.BoundRelativeLayout_brl_maxHeight, maxHeight);

            widthMode = array.getInt(R.styleable.BoundRelativeLayout_brl_widthMode, widthMode);
            heightMode = array.getInt(R.styleable.BoundRelativeLayout_brl_heightMode, heightMode);

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

    private int getMeasureSpec(int measureSpec, int mode, int maxSize) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        boolean noBoundary = maxSize == UNDEFINED_SIZE;

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                resultSize = noBoundary ? specSize : Math.min(specSize, maxSize);

                if (mode == MODE_MATCH) {
                    resultMode = MeasureSpec.EXACTLY;
                } else if (mode == MODE_WRAP) {
                    resultMode = MeasureSpec.AT_MOST;
                } else {
                    resultMode = specMode;
                }
                break;

            case MeasureSpec.UNSPECIFIED:
                resultSize = noBoundary ? specSize : Math.min(specSize, maxSize);

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

    @Override
    protected int getSuggestedMinimumWidth() {
        int suggestedMinimumWidth = super.getSuggestedMinimumWidth();
        return Math.max(minWidth, suggestedMinimumWidth);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        int suggestedMinimumHeight = super.getSuggestedMinimumHeight();
        return Math.max(minHeight, suggestedMinimumHeight);
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

        measuredWidth = Math.max(measuredWidth, getSuggestedMinimumWidth());
        measuredHeight = Math.max(measuredHeight, getSuggestedMinimumHeight());

        if (this.minWidth != UNDEFINED_SIZE)
            measuredWidth = Math.max(measuredWidth, this.minWidth);
        if (this.minHeight != UNDEFINED_SIZE)
            measuredHeight = Math.max(measuredHeight, this.minHeight);

        //Set measured dimension
        setMeasuredDimension(
                resolveSizeAndState(measuredWidth, widthMeasureSpec, 0),
                resolveSizeAndState(measuredHeight, heightMeasureSpec, 0)
        );
    }
}
