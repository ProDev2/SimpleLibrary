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
            image = ImageTools.cropBitmap(image);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, File file) {
        if (loader != null && file != null)
            loader.cancelRequest(file.getAbsolutePath());
    }
}
