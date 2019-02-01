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
            image = ImageTools.cropBitmap(image);
        return image;
    }

    public static void cancelRequest(ImageLoader loader, File file) {
        if (loader != null && file != null)
            loader.cancelRequest(file.getAbsolutePath());
    }
}