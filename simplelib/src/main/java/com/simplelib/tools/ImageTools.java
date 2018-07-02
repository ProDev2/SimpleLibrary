package com.simplelib.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.simplelib.interfaces.OnFinish;

import java.io.File;
import java.io.FileOutputStream;

public class ImageTools {
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

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);

        return output;
    }

    public static Bitmap resizedBitmapInDp(Bitmap bitmap, int newWidthDp, int newHeightDp) {
        return resizedBitmap(bitmap, dpToPx(newWidthDp), dpToPx(newHeightDp));
    }

    public static Bitmap resizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
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
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        int x = (bitmap.getWidth() / 2) - (size / 2);
        int y = (bitmap.getHeight() / 2) - (size / 2);

        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }

    public static Bitmap cropBitmap(Bitmap bitmap) {
        return cropBitmap(bitmap, 0);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int imageOffset) {
        bitmap = cutOutSquare(bitmap);

        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        int color = 0xff424242;
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        int dpImageOffset = imageOffset != 0 ? dpToPx(imageOffset) : 0;

        Rect rect = new Rect(dpImageOffset, dpImageOffset, size - dpImageOffset, size - dpImageOffset);
        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
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
        bitmap = cropBitmap(bitmap);

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

        output = cropBitmap(output);

        return output;
    }

    public static Bitmap addRoundBorder(Bitmap bitmap, int color, int borderColor, int borderWidth) {
        return addRoundBorder(bitmap, color, borderColor, borderWidth, 0);
    }

    public static Bitmap addRoundBorder(Bitmap bitmap, int color, int borderColor, int borderWidth, int imageOffset) {
        bitmap = cropBitmap(bitmap);

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

        output = cropBitmap(output);

        return output;
    }

    public static Drawable createRoundBackgroundDrawable(Resources resources, int size, int color) {
        return new BitmapDrawable(resources, createRoundBackground(size, color));
    }

    public static Bitmap createRoundBackground(int size, int color) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(color);

        output = cropBitmap(output);

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
}