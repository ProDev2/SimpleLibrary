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
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageTools;

import java.io.File;

public abstract class FileVideoThumbnailRequest extends ImageLoader.ImageRequest {
    private File file;
    private int type;

    private boolean cropRound;

    public FileVideoThumbnailRequest(File file) {
        this(file, MediaStore.Images.Thumbnails.MICRO_KIND);
    }

    public FileVideoThumbnailRequest(File file, int type) {
        this(file, type, false);
    }

    public FileVideoThumbnailRequest(File file, int type, boolean cropRound) {
        super(file.getAbsolutePath());

        this.file = file;
        this.type = type;

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
        Bitmap image = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), type);
        if (cropRound)
            image = ImageTools.cropBitmap(image, true);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, File file) {
        if (loader != null && file != null)
            loader.cancelRequest(file.getAbsolutePath());
    }
}
