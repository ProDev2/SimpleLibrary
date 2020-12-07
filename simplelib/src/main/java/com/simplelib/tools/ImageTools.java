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

package com.simplelib.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.simplelib.interfaces.OnFinish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class ImageTools {
    public static boolean isImageFile(Context context, Uri uri) {
        try {
            if (context != null && uri != null)
                return isImageFile(context.getContentResolver().openInputStream(uri));
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isImageFile(File file) {
        try {
            if (file != null)
                return isImageFile(new FileInputStream(file));
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isImageFile(InputStream in) {
        try {
            if (in != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                return options.outWidth != -1 && options.outHeight != -1;
            }
        } catch (Exception e) {
        } finally {
            SessionTools.closeWithoutFail(in);
        }
        return false;
    }

    public static Bitmap captureImageOfView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);

        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);

        return resultBitmap;
    }

    public static Bitmap darkenBitmap(Bitmap image, float manipulateValue) {
        try {
            if (!image.isMutable())
                image = image.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e) {
        }

        try {
            int alpha = Math.round(manipulateValue * 255f);
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;

            Canvas canvas = new Canvas(image);
            canvas.drawARGB(alpha,0,0,0);
            canvas.drawBitmap(image, new Matrix(), new Paint());
        } catch (Exception e) {
        }

        return image;
    }

    @SuppressLint("NewApi")
    public static Bitmap blurImage(RenderScript renderScript, Bitmap image, float blurRadius) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Allocation input = Allocation.createFromBitmap(renderScript, image);
                Allocation output = Allocation.createTyped(renderScript, input.getType());
                ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                script.setRadius(blurRadius);
                script.setInput(input);
                script.forEach(output);
                output.copyTo(image);
            }
        } catch (Exception e) {
        }
        return image;
    }

    public static Drawable copyDrawable(Drawable drawable) {
        try {
            if (drawable != null)
                return drawable.getConstantState().newDrawable().mutate();
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);

        return output;
    }

    public static Drawable getDrawable(Context context, Bitmap image) {
        try {
            return new BitmapDrawable(context.getResources(), image);
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable)
                return ((BitmapDrawable) drawable).getBitmap();

            try {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                return bitmap;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Bitmap getBitmap(Drawable drawable, int reqWidth, int reqHeight) {
        if (drawable != null) {
            if (reqWidth <= 0 || reqHeight <= 0)
                return null;

            if (drawable instanceof BitmapDrawable) {
                Bitmap image = ((BitmapDrawable) drawable).getBitmap();
                if (image != null)
                    return fitImageIn(image, reqWidth, reqHeight, false, -1, 0);
                return image;
            }

            try {
                int drawableWidth = drawable.getIntrinsicWidth();
                int drawableHeight = drawable.getIntrinsicHeight();

                double ratio = getInsideRatio(drawableWidth, drawableHeight, reqWidth, reqHeight);
                int width = (int) ((double) drawableWidth * ratio);
                int height = (int) ((double) drawableHeight * ratio);

                if (width <= 0 || height <= 0) {
                    width = reqWidth;
                    height = reqHeight;
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                return bitmap;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Bitmap resizeBitmapInDp(Bitmap bitmap, int newWidthDp, int newHeightDp) {
        return resizeBitmap(bitmap, dpToPx(newWidthDp), dpToPx(newHeightDp));
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static Bitmap cutOutSquare(Bitmap bitmap) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        int size = Math.min(srcWidth, srcHeight);

        int x = (srcWidth / 2) - (size / 2);
        int y = (srcHeight / 2) - (size / 2);

        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }

    public static Bitmap fitImageIn(Bitmap bitmap) {
        return fitImageIn(bitmap, false);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, boolean round) {
        return fitImageIn(bitmap, round, 0);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, boolean round, int imageOffset) {
        return fitImageIn(bitmap, round, -1, imageOffset);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, boolean round, int cornerRadius, int imageOffset) {
        if (bitmap == null)
            throw new NullPointerException("Image cannot be null");

        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        return fitImageIn(bitmap, srcWidth, srcHeight, round, cornerRadius, imageOffset);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, int width, int height) {
        return fitImageIn(bitmap, width, height, false);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, int width, int height, boolean round) {
        return fitImageIn(bitmap, width, height, round, 0);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, int width, int height, boolean round, int imageOffset) {
        return fitImageIn(bitmap, width, height, round, -1, imageOffset);
    }

    public static Bitmap fitImageIn(Bitmap bitmap, int width, int height, boolean round, int cornerRadius, int imageOffset) {
        if (bitmap == null)
            throw new NullPointerException("Image cannot be null");

        int size = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (round) {
            int color = 0xff424242;

            paint.setColor(color);
            if (cornerRadius < 0)
                canvas.drawCircle(width / 2, height / 2, size / 2, paint);
            else
                canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }

        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        double ratio = getInsideRatio(
                srcWidth,
                srcHeight,
                width - imageOffset,
                height - imageOffset);

        int nextWidth = (int) ((double) srcWidth * ratio);
        int nextHeight = (int) ((double) srcHeight * ratio);

        if (nextWidth > 0 && nextHeight > 0) {
            Rect rect = new Rect((width / 2) - (nextWidth / 2),
                    (height / 2) - (nextHeight / 2),
                    (width / 2) + (nextWidth / 2),
                    (height / 2) + (nextHeight / 2));
            canvas.drawBitmap(bitmap, null, rect, paint);
        }

        return output;
    }

    public static Bitmap cropBitmap(Bitmap bitmap) {
        return cropBitmap(bitmap, false);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, boolean round) {
        return cropBitmap(bitmap, round, 0);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, boolean round, int imageOffset) {
        return cropBitmap(bitmap, round, -1, imageOffset);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, boolean round, int cornerRadius, int imageOffset) {
        if (bitmap == null)
            throw new NullPointerException("Image cannot be null");

        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        return cropBitmap(bitmap, srcWidth, srcHeight, round, cornerRadius, imageOffset);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int width, int height) {
        return cropBitmap(bitmap, width, height, false);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int width, int height, boolean round) {
        return cropBitmap(bitmap, width, height, round, 0);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int width, int height, boolean round, int imageOffset) {
        return cropBitmap(bitmap, width, height, round, -1, imageOffset);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int width, int height, boolean round, int cornerRadius, int imageOffset) {
        if (bitmap == null)
            throw new NullPointerException("Image cannot be null");

        int size = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (round) {
            int color = 0xff424242;

            paint.setColor(color);
            if (cornerRadius < 0)
                canvas.drawCircle(width / 2, height / 2, size / 2, paint);
            else
                canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }

        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        double ratio = getOutsideRatio(
                srcWidth,
                srcHeight,
                width - imageOffset,
                height - imageOffset);

        int nextWidth = (int) ((double) srcWidth * ratio);
        int nextHeight = (int) ((double) srcHeight * ratio);

        if (nextWidth > 0 && nextHeight > 0) {
            Rect rect = new Rect((width / 2) - (nextWidth / 2),
                    (height / 2) - (nextHeight / 2),
                    (width / 2) + (nextWidth / 2),
                    (height / 2) + (nextHeight / 2));
            canvas.drawBitmap(bitmap, null, rect, paint);
        }

        return output;
    }

    public static double getInsideRatio(int width, int height, int widthTo, int heightTo) {
        double widthRatio = width != 0 ? (double) widthTo / (double) width : 1d;
        double heightRatio = height != 0 ? (double) heightTo / (double) height : 1d;

        return Math.min(widthRatio, heightRatio);
    }

    public static double getOutsideRatio(int width, int height, int widthTo, int heightTo) {
        double widthRatio = width != 0 ? (double) widthTo / (double) width : 1d;
        double heightRatio = height != 0 ? (double) heightTo / (double) height : 1d;

        return Math.max(widthRatio, heightRatio);
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

    public static Bitmap addRoundBackground(Bitmap bitmap, int color, int borderWidth) {
        bitmap = cropBitmap(bitmap, true);

        int size = Math.max(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);

        int x = (size / 2) - (bitmap.getWidth() / 2);
        int y = (size / 2) - (bitmap.getHeight() / 2);

        if (x > 0) x = 0;
        if (y > 0) y = 0;

        bitmap = Bitmap.createBitmap(bitmap, -x, -y, size, size);

        int dpBorderWidth = borderWidth != 0 ? dpToPx(borderWidth) : 0;

        Rect rect = new Rect(dpBorderWidth, dpBorderWidth, size - dpBorderWidth, size - dpBorderWidth);
        canvas.drawBitmap(bitmap, null, rect, null);

        output = cropBitmap(output, true);

        return output;
    }

    public static Bitmap addRoundBorder(Bitmap bitmap, int color, int borderColor, int borderWidth) {
        return addRoundBorder(bitmap, color, borderColor, borderWidth, 0);
    }

    public static Bitmap addRoundBorder(Bitmap bitmap, int color, int borderColor, int borderWidth, int imageOffset) {
        bitmap = cropBitmap(bitmap, true);

        int size = Math.max(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        int x = (size / 2) - (bitmap.getWidth() / 2);
        int y = (size / 2) - (bitmap.getHeight() / 2);

        if (x > 0) x = 0;
        if (y > 0) y = 0;

        int dpBorderWidth = borderWidth != 0 ? dpToPx(borderWidth) : 0;

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        canvas.drawCircle(size / 2, size / 2, size / 2, borderPaint);

        Paint fillPaint = new Paint();
        fillPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(color);
        canvas.drawCircle(size / 2, size / 2, (size / 2) - dpBorderWidth, fillPaint);

        int dpImageOffset = imageOffset != 0 ? dpToPx(imageOffset) : 0;

        bitmap = Bitmap.createBitmap(bitmap, -x, -y, size, size);

        int start = dpBorderWidth + dpImageOffset;
        int end = size - (dpBorderWidth + dpImageOffset);
        Rect rect = new Rect(start, start, end, end);
        canvas.drawBitmap(bitmap, null, rect, null);

        output = cropBitmap(output, true);

        return output;
    }

    public static Drawable createRoundBackgroundDrawable(Resources resources, int size, int color) {
        return new BitmapDrawable(resources, createRoundBackground(size, color));
    }

    public static Bitmap createRoundBackground(int size, int color) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);

        output = cropBitmap(output, true);

        return output;
    }

    public static int manipulateColor(int color, float manipulateValue) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= manipulateValue;
        return Color.HSVToColor(hsv);
    }

    public static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static void saveImage(final FragmentActivity activity, final File path, final Bitmap image, final Bitmap.CompressFormat format, final OnFinish finishListener) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            image.compress(format, 100, outputStream);

            MediaScannerConnection.scanFile(activity, new String[]{path.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finishListener != null)
                                finishListener.onFinish();
                        }
                    });
                }
            });
        } catch (Exception e) {
            if (finishListener != null)
                finishListener.onError();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static class ImageBlur extends AsyncTask<Bitmap, Void, Void> {
        private static RenderScript renderScript;

        public static boolean blur(Context context, OnBlurListener listener, float blurRadius, Bitmap... images) {
            return new ImageBlur(context).setBlurListener(listener).blur(blurRadius, images);
        }

        public static boolean blur(Context context, OnBlurListener listener, float sampleSize, float blurRadius, Bitmap... images) {
            return new ImageBlur(context).setBlurListener(listener).blur(sampleSize, blurRadius, images);
        }

        public static boolean blur(Context context, OnBlurListener listener, Executor executor, float blurRadius, Bitmap... images) {
            return new ImageBlur(context).setBlurListener(listener).blur(executor, blurRadius, images);
        }

        public static boolean blur(Context context, OnBlurListener listener, Executor executor, float sampleSize, float blurRadius, Bitmap... images) {
            return new ImageBlur(context).setBlurListener(listener).blur(executor, sampleSize, blurRadius, images);
        }

        private List<Bitmap> imageList;

        private float sampleSize;
        private float blurRadius;

        private OnBlurListener listener;

        public ImageBlur(Context context) {
            try {
                if (context != null && renderScript == null)
                    renderScript = RenderScript.create(context);
            } catch (Exception e) {
            }

            imageList = new ArrayList<>();
        }

        public boolean blur(float blurRadius, Bitmap... images) {
            return blur(1f, blurRadius, images);
        }

        public boolean blur(float sampleSize, float blurRadius, Bitmap... images) {
            return blur(AsyncTask.SERIAL_EXECUTOR, sampleSize, blurRadius, images);
        }

        public boolean blur(Executor executor, float blurRadius, Bitmap... images) {
            return blur(executor, 1f, blurRadius, images);
        }

        public boolean blur(Executor executor, float sampleSize, float blurRadius, Bitmap... images) {
            try {
                if (!isCancelled()) {
                    imageList.clear();
                    if (images != null && images.length > 0)
                        imageList.addAll(Arrays.asList(images));

                    this.sampleSize = sampleSize;
                    this.blurRadius = blurRadius;

                    executeOnExecutor(executor);
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            try {
                if (listener != null)
                    listener.onStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Bitmap... images) {
            if (renderScript == null) return null;

            try {
                if (images != null && images.length > 0)
                    imageList.addAll(Arrays.asList(images));

                if (imageList.size() > 0) {
                    for (int pos = 0; pos < imageList.size(); pos++) {
                        try {
                            Bitmap image = imageList.get(pos);

                            if (image == null) continue;

                            int width = image.getWidth();
                            int height = image.getHeight();

                            if (width <= 0 || height <= 0) continue;

                            try {
                                if (sampleSize != 1) {
                                    int sizeX = (int) ((float) width * sampleSize);
                                    int sizeY = (int) ((float) height * sampleSize);

                                    if (sizeX > 0 && sizeY > 0) {
                                        image = Bitmap.createScaledBitmap(image, sizeX, sizeY, true);
                                        image = Bitmap.createScaledBitmap(image, width, height, true);
                                    }
                                }
                            } catch (Exception e) {
                            }

                            try {
                                if (listener != null) {
                                    Bitmap modifiedImage = listener.modifyImageBeforeBlur(image);
                                    if (modifiedImage != null)
                                        image = modifiedImage;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            image = blurImage(renderScript, image, blurRadius);

                            try {
                                if (listener != null) {
                                    Bitmap modifiedImage = listener.modifyImageAfterBlur(image);
                                    if (modifiedImage != null)
                                        image = modifiedImage;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            imageList.set(pos, image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (listener != null)
                    listener.onFinish(imageList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ImageBlur setBlurListener(OnBlurListener listener) {
            this.listener = listener;
            return this;
        }

        public static abstract class OnBlurListener {
            public void onStart() {
            }

            public Bitmap modifyImageBeforeBlur(Bitmap image) {
                return image;
            }

            public Bitmap modifyImageAfterBlur(Bitmap image) {
                return image;
            }

            public abstract void onFinish(List<Bitmap> images);
        }
    }
}