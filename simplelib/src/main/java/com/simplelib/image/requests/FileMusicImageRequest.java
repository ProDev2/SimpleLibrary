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

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageLoaderTools;
import com.simplelib.tools.ImageTools;

import java.io.File;

public abstract class FileMusicImageRequest extends ImageLoader.ImageRequest {
    private File file;
    private int reqWidth, reqHeight;

    private boolean cropRound;

    public FileMusicImageRequest(File file) {
        this(file, -1);
    }

    public FileMusicImageRequest(File file, int reqSize) {
        this(file, reqSize, reqSize);
    }

    public FileMusicImageRequest(File file, int reqWidth, int reqHeight) {
        this(file, reqWidth, reqHeight, false);
    }

    public FileMusicImageRequest(File file, int reqWidth, int reqHeight, boolean cropRound) {
        super(file.getAbsolutePath());

        this.file = file;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;

        this.cropRound = cropRound;
    }

    public File getFile() {
        return file;
    }

    public void setCropRound(boolean cropRound) {
        this.cropRound = cropRound;
    }

    @Override
    public Bitmap onLoad() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());

        byte[] imageBytes = retriever.getEmbeddedPicture();

        Bitmap image = ImageLoaderTools.loadInReqSize(imageBytes, reqWidth, reqHeight);
        if (cropRound)
            image = ImageTools.cropBitmap(image, true);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, File file) {
        if (loader != null && file != null)
            loader.cancelRequest(file.getAbsolutePath());
    }
}
