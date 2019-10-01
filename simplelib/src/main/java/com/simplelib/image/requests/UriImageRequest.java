package com.simplelib.image.requests;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageLoaderTools;
import com.simplelib.tools.ImageTools;

public abstract class UriImageRequest extends ImageLoader.ImageRequest {
    private Context context;
    private Uri uri;

    private int reqWidth, reqHeight;

    private boolean cropRound;

    public UriImageRequest(Context context, Uri uri) {
        this(context, uri, -1);
    }

    public UriImageRequest(Context context, Uri uri, int reqSize) {
        this(context, uri, reqSize, reqSize);
    }

    public UriImageRequest(Context context, Uri uri, int reqWidth, int reqHeight) {
        this(context, uri, reqWidth, reqHeight, false);
    }

    public UriImageRequest(Context context, Uri uri, int reqWidth, int reqHeight, boolean cropRound) {
        super(uri.toString());

        this.context = context;
        this.uri = uri;

        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;

        this.cropRound = cropRound;
    }

    public Uri getUri() {
        return uri;
    }

    public void setCropRound(boolean cropRound) {
        this.cropRound = cropRound;
    }

    @Override
    public Bitmap onLoad() {
        Bitmap image = ImageLoaderTools.loadInReqSize(context, uri, reqWidth, reqHeight);
        if (cropRound)
            image = ImageTools.cropBitmap(image, true);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, Uri uri) {
        if (loader != null && uri != null)
            loader.cancelRequest(uri.toString());
    }
}
