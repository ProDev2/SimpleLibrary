/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.simplelib.R;

public class BoundLayout extends ViewGroup {
    public static final int UNDEFINED_SIZE = -1;

    public static final int MODE_NONE = 0;
    public static final int MODE_WRAP = 1;
    public static final int MODE_MATCH = 2;

    private boolean forceMin;

    private int minWidth, minHeight;
    private int maxWidth, maxHeight;

    private int widthMode, heightMode;

    public BoundLayout(@NonNull Context context) {
        super(context);
        initialize(null, 0, 0);
    }

    public BoundLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0, 0);
    }

    public BoundLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BoundLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BoundLayout, defStyleAttr, defStyleRes);

            forceMin = array.getBoolean(R.styleable.BoundRelativeLayout_brl_force_min, forceMin);

            minWidth = array.getDimensionPixelSize(R.styleable.BoundLayout_bl_minWidth, minWidth);
            minHeight = array.getDimensionPixelSize(R.styleable.BoundLayout_bl_minHeight, minHeight);

            maxWidth = array.getDimensionPixelSize(R.styleable.BoundLayout_bl_maxWidth, maxWidth);
            maxHeight = array.getDimensionPixelSize(R.styleable.BoundLayout_bl_maxHeight, maxHeight);

            widthMode = array.getInt(R.styleable.BoundLayout_bl_widthMode, widthMode);
            heightMode = array.getInt(R.styleable.BoundLayout_bl_heightMode, heightMode);

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

    private void onMeasureSelf(int widthMeasureSpec, int heightMeasureSpec) {
        //Calculate constraints
        int widthConstraints = getPaddingLeft() + getPaddingRight();
        int heightConstraints = getPaddingTop() + getPaddingBottom();

        //Find rightmost and bottom-most child
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        for (int pos = 0; pos < count; pos++) {
            View child = getChildAt(pos);
            if (child == null) continue;

            int width = MeasureSpec.UNSPECIFIED;
            int height = MeasureSpec.UNSPECIFIED;

            int marginWidth = 0;
            int marginHeight = 0;

            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params != null) {
                width = params.width;
                height = params.height;
            }

            if (params instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) params;
                marginWidth = lp.leftMargin + lp.rightMargin;
                marginHeight = lp.topMargin + lp.bottomMargin;
            } else if (params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                marginWidth = lp.leftMargin + lp.rightMargin;
                marginHeight = lp.topMargin + lp.bottomMargin;
            }

            if (marginWidth < 0) marginWidth = 0;
            if (marginHeight < 0) marginHeight = 0;

            int childWidthMeasureSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    widthConstraints + marginWidth,
                    width
            );

            int childHeightMeasureSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    heightConstraints + marginHeight,
                    height
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if (child.getVisibility() != GONE) {
                int childRight = child.getMeasuredWidth() + marginWidth;
                int childBottom = child.getMeasuredHeight() + marginHeight;

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        //Account for padding too
        maxWidth += widthConstraints;
        maxHeight += heightConstraints;

        //Check against our foreground's minimum height and width
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Drawable foregroundDrawable = getForeground();
            if (foregroundDrawable != null) {
                maxWidth = Math.max(maxWidth, foregroundDrawable.getMinimumWidth());
                maxHeight = Math.max(maxHeight, foregroundDrawable.getMinimumHeight());
            }
        }

        //Check against minimum height and width
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        //Set measured dimension
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Make measure spec
        widthMeasureSpec = getMeasureSpec(widthMeasureSpec, this.widthMode, this.maxWidth);
        heightMeasureSpec = getMeasureSpec(heightMeasureSpec, this.heightMode, this.maxHeight);

        //Measure layout
        onMeasureSelf(widthMeasureSpec, heightMeasureSpec);

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
            onMeasureSelf(widthMeasureSpec, heightMeasureSpec);

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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //Calculate constraints
        final int boundsLeft = this.getPaddingLeft();
        final int boundsTop = this.getPaddingTop();
        final int boundsRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int boundsBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        final int width = boundsRight - boundsLeft;
        final int height = boundsBottom - boundsTop;

        final int centerX = boundsLeft + (width / 2);
        final int centerY = boundsTop + (height / 2);

        //Calculate layout
        int count = getChildCount();

        for (int pos = 0; pos < count; pos++) {
            View child = getChildAt(pos);
            if (child == null) continue;

            int leftMargin = 0;
            int topMargin = 0;
            int rightMargin = 0;
            int bottomMargin = 0;

            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) params;
                leftMargin = lp.leftMargin;
                topMargin = lp.topMargin;
                rightMargin = lp.rightMargin;
                bottomMargin = lp.bottomMargin;
            } else if (params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                leftMargin = lp.leftMargin;
                topMargin = lp.topMargin;
                rightMargin = lp.rightMargin;
                bottomMargin = lp.bottomMargin;
            }

            if (child.getVisibility() != GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                int offsetX = (width - childWidth) / 2;
                int offsetY = (height - childHeight) / 2;

                int offsetLeft = Math.max(0, leftMargin - offsetX);
                int offsetTop = Math.max(0, topMargin - offsetY);
                int offsetRight = Math.min(0, offsetX - rightMargin);
                int offsetBottom = Math.min(0, offsetY - bottomMargin);

                int centerPosX = centerX + offsetLeft + offsetRight;
                int centerPosY = centerY + offsetTop + offsetBottom;

                int childLeft = centerPosX - (childWidth / 2);
                int childTop = centerPosY - (childHeight / 2);
                int childRight = centerPosX + (childWidth / 2);
                int childBottom = centerPosY + (childHeight / 2);

                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            if (params instanceof LayoutParams)
                return new LayoutParams((LayoutParams) params);
            else if (params instanceof ViewGroup.MarginLayoutParams)
                return new LayoutParams((ViewGroup.MarginLayoutParams) params);
            else
                return new LayoutParams((ViewGroup.LayoutParams) params);
        }
        return null;
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

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
