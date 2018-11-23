package com.simplelib.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.InputStream;

public class ImageLoaderTools {
    public static Bitmap loadInReqSize(File path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (reqWidth >= 0 && reqHeight >= 0) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path.getAbsolutePath(), options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
        }
        return BitmapFactory.decodeFile(path.getAbsolutePath(), options);
    }

    public static Bitmap loadInReqSize(InputStream stream, int reqWidth, int reqHeight) {
        if (stream == null) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        if (reqWidth >= 0 && reqHeight >= 0) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
        }
        return BitmapFactory.decodeStream(stream, null, options);
    }

    public static Bitmap loadInReqSize(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (reqWidth >= 0 && reqHeight >= 0) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2;
        }

        return inSampleSize;
    }
}