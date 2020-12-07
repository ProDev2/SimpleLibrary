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

package com.simplelib.image.requests;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageTools;

public abstract class TextImageRequest extends ImageLoader.ImageRequest {
    private String text;
    private int reqWidth, reqHeight;

    private boolean cropRound;

    private int backColor = Color.parseColor("#EEEEEE");
    private float textSize = 72;
    private int textScaleX = 1;
    private int textColor = Color.BLACK;
    private Typeface textFont;

    public TextImageRequest(String text) {
        this(text, -1);
    }

    public TextImageRequest(String text, int reqSize) {
        this(text, reqSize, reqSize);
    }

    public TextImageRequest(String text, int reqWidth, int reqHeight) {
        this(text, reqWidth, reqHeight, false);
    }

    public TextImageRequest(String text, int reqWidth, int reqHeight, boolean cropRound) {
        super("string:" + text);

        this.text = text;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;

        this.cropRound = cropRound;
    }

    private static Bitmap createImage(int width, int height, int color, Paint textPaint, String text) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);

        Rect rect = new Rect();
        canvas.getClipBounds(rect);
        int cHeight = rect.height();
        int cWidth = rect.width();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.getTextBounds(text, 0, text.length(), rect);

        float x = cWidth / 2f - rect.width() / 2f - rect.left;
        float y = cHeight / 2f + rect.height() / 2f - rect.bottom;
        canvas.drawText(text, x, y, textPaint);

        return bitmap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setTextSizeInSp(float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics));
    }

    public void setTextScaleX(int textScaleX) {
        this.textScaleX = textScaleX;
    }

    public void setTextFont(Context context, String assetPath) {
        try {
            setTextFont(Typeface.createFromAsset(context.getAssets(), assetPath));
        } catch (Exception e) {
        }
    }

    public void setTextFont(Typeface textFont) {
        this.textFont = textFont;
    }

    public void setCropRound(boolean cropRound) {
        this.cropRound = cropRound;
    }

    @Override
    public Bitmap onLoad() {
        int sizeX = reqWidth;
        int sizeY = reqHeight;

        if (sizeX < 0 || sizeY < 0) sizeX = sizeY = 100;

        Paint textPaint = new Paint();
        if (textFont != null)
            textPaint.setTypeface(textFont);
        textPaint.setTextSize(textSize);
        textPaint.setTextScaleX(textScaleX);
        textPaint.setColor(textColor);

        Bitmap image = createImage(sizeX, sizeY, backColor, textPaint, text);
        if (cropRound)
            image = ImageTools.cropBitmap(image, true);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, String text) {
        if (loader != null && text != null)
            loader.cancelRequest("string:" + text);
    }
}
