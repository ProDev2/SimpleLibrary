package com.simplelib.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

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

    public static Bitmap loadInReqSize(Context context, Uri uri, int reqWidth, int reqHeight) {
        if (context == null || uri == null) return null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;

                InputStream stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                options.inJustDecodeBounds = false;
            }

            InputStream stream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(stream, null, options);
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap loadInReqSize(StreamFetcher fetcher, int reqWidth, int reqHeight) {
        if (fetcher == null) return null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;

                InputStream stream = openInputStream(fetcher);
                if (stream == null) return null;

                BitmapFactory.decodeStream(stream, null, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
            }

            InputStream stream = openInputStream(fetcher);
            if (stream == null) return null;

            return BitmapFactory.decodeStream(stream, null, options);
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap loadInReqSize(InputStream stream, int reqWidth, int reqHeight) {
        if (stream == null) return null;

        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        Bitmap image = ImageTools.fitImageIn(bitmap, reqWidth, reqHeight);

        try {
            if (!bitmap.isRecycled())
                bitmap.recycle();
        } catch (Exception e) {
        }

        return image;
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
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            while ((halfWidth / inSampleSize) >= reqWidth || (halfHeight / inSampleSize) >= reqHeight)
                inSampleSize *= 2;
        }

        return inSampleSize;
    }

    private static InputStream openInputStream(StreamFetcher fetcher) {
        InputStream stream = null;
        try {
            if (fetcher != null)
                stream = fetcher.openInputStream();
        } catch (Exception e) {
        }
        return stream;
    }

    public interface StreamFetcher {
        InputStream openInputStream();
    }
}