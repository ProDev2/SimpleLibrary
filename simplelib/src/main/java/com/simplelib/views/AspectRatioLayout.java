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
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

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

                if (newHeight > height && heightMode != MeasureSpec.UNSPECIFIED)
                    width *= (float) height / (float) newHeight;
                else
                    height = newHeight;
                heightMode = MeasureSpec.AT_MOST;
            } else if (heightMode == MeasureSpec.EXACTLY) {
                int newWidth = Math.round(widthRatio / heightRatio * height);

                if (newWidth > width && widthMode != MeasureSpec.UNSPECIFIED)
                    height *= (float) width / (float) newWidth;
                else
                    width = newWidth;
                widthMode = MeasureSpec.AT_MOST;
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