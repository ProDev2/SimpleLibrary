package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;

import com.simplelib.R;

public class AspectRatioLayout extends RelativeLayout {
    private static final String TAG = "AspectRatioLayout";

    private float widthRatio;
    private float heightRatio;

    public AspectRatioLayout(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public AspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        widthRatio = -1;
        heightRatio = -1;

        try {
            if (context != null) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout, defStyleAttr, defStyleRes);

                widthRatio = a.getFloat(R.styleable.AspectRatioLayout_arl_widthRatio, widthRatio);
                heightRatio = a.getFloat(R.styleable.AspectRatioLayout_arl_heightRatio, heightRatio);

                a.recycle();
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthRatio > 0 && heightRatio > 0) {
            if (widthMode == MeasureSpec.EXACTLY) {
                int newHeight = Math.round(heightRatio / widthRatio * width);

                if (heightMode == MeasureSpec.UNSPECIFIED) {
                    height = newHeight;
                    heightMode = MeasureSpec.AT_MOST;
                } else if (heightMode == MeasureSpec.AT_MOST) {
                    if (newHeight > height)
                        width *= (float) height / (float) newHeight;
                    else
                        height = newHeight;
                    heightMode = MeasureSpec.AT_MOST;
                }
            } else if (heightMode == MeasureSpec.EXACTLY) {
                int newWidth = Math.round(widthRatio / heightRatio * height);

                if (widthMode == MeasureSpec.UNSPECIFIED) {
                    width = newWidth;
                    widthMode = MeasureSpec.AT_MOST;
                } else if (widthMode == MeasureSpec.AT_MOST) {
                    if (newWidth > width)
                        height *= (float) width / (float) newWidth;
                    else
                        width = newWidth;
                    widthMode = MeasureSpec.AT_MOST;
                }
            } else {
                Log.w(TAG, "Width or height are not exact or at most, so do nothing.");
            }
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public float getWidthRatio() {
        return widthRatio;
    }

    public float getHeightRatio() {
        return heightRatio;
    }

    public float getAspectRatio() {
        if (widthRatio == heightRatio) return 1;
        if (widthRatio <= 0 || heightRatio <= 0) return -1;

        return widthRatio / heightRatio;
    }

    public void setAspectRatio(float widthRatio, float heightRatio) {
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
        requestLayout();
    }
}