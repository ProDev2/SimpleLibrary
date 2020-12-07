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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.InputStream;

public class ImageLoaderTools {
    public static Bitmap loadInReqSize(File path, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path.getAbsolutePath(), options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                options.inJustDecodeBounds = false;
            }
            return BitmapFactory.decodeFile(path.getAbsolutePath(), options);
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        }
        return null;
    }

    public static Bitmap loadInReqSize(Context context, Uri uri, int reqWidth, int reqHeight) {
        if (context == null || uri == null) return null;

        InputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;

                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                options.inJustDecodeBounds = false;
            }

            if (stream != null)
                SessionTools.closeWithoutFail(stream);

            stream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(stream, null, options);
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        } finally {
            SessionTools.closeWithoutFail(stream);
        }
        return null;
    }

    public static Bitmap loadInReqSize(StreamFetcher fetcher, int reqWidth, int reqHeight) {
        if (fetcher == null) return null;

        InputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;

                stream = openInputStream(fetcher);
                if (stream == null) return null;

                BitmapFactory.decodeStream(stream, null, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
            }

            if (stream != null)
                SessionTools.closeWithoutFail(stream);

            stream = openInputStream(fetcher);
            if (stream == null) return null;

            return BitmapFactory.decodeStream(stream, null, options);
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        } finally {
            SessionTools.closeWithoutFail(stream);
        }
        return null;
    }

    public static Bitmap loadInReqSize(InputStream stream, int reqWidth, int reqHeight) {
        if (stream == null) return null;

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Bitmap image = ImageTools.fitImageIn(bitmap, reqWidth, reqHeight, false, -1, 0);

            try {
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            } catch (Exception e) {
            }

            return image;
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        } finally {
            SessionTools.closeWithoutFail(stream);
        }
        return null;
    }

    public static Bitmap loadInReqSize(byte[] data, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (reqWidth >= 0 && reqHeight >= 0) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, options);

                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                options.inJustDecodeBounds = false;
            }
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        }
        return null;
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