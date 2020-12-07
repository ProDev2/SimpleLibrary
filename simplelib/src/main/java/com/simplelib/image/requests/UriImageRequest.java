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
