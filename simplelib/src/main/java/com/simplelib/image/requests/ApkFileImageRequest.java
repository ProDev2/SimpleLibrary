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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageTools;

import java.io.File;

public abstract class ApkFileImageRequest extends ImageLoader.ImageRequest {
    private Context context;

    private File apkFile;
    private int reqWidth, reqHeight;

    private boolean cropRound;

    private PackageManager packageManager;
    private PackageInfo packageInfo;

    public ApkFileImageRequest(Context context, File apkFile) {
        this(context, apkFile, -1);
    }

    public ApkFileImageRequest(Context context, File apkFile, int reqSize) {
        this(context, apkFile, reqSize, reqSize);
    }

    public ApkFileImageRequest(Context context, File apkFile, int reqWidth, int reqHeight) {
        this(context, apkFile, reqWidth, reqHeight, false);
    }

    public ApkFileImageRequest(Context context, File apkFile, int reqWidth, int reqHeight, boolean cropRound) {
        super(apkFile.getAbsolutePath());

        this.context = context;

        this.apkFile = apkFile;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;

        this.cropRound = cropRound;
    }

    public File getApkFile() {
        return apkFile;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setCropRound(boolean cropRound) {
        this.cropRound = cropRound;
    }

    @Override
    public Bitmap onLoad() {
        try {
            packageManager = context.getPackageManager();
            if (apkFile.exists() && apkFile.isFile()) {
                packageInfo = packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);

                packageInfo.applicationInfo.sourceDir = apkFile.getAbsolutePath();
                packageInfo.applicationInfo.publicSourceDir = apkFile.getAbsolutePath();
            }
        } catch (Exception e) {
        }

        if (packageManager != null && packageInfo != null) {
            Drawable iconDrawable = packageInfo.applicationInfo.loadIcon(packageManager);
            Bitmap image = convertToBitmap(iconDrawable, reqWidth, reqHeight);
            if (cropRound)
                image = ImageTools.cropBitmap(image, true);
            return image;
        }

        return null;
    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    @Override
    public void applyTo(ImageLoader.ImageRequest request) {
        super.applyTo(request);

        if (request instanceof ApkFileImageRequest) {
            ApkFileImageRequest apkFileImageRequest = (ApkFileImageRequest) request;

            apkFileImageRequest.packageManager = this.packageManager;
            apkFileImageRequest.packageInfo = this.packageInfo;
        }
    }

    public static void cancelRequest(ImageLoader loader, File file) {
        if (loader != null && file != null)
            loader.cancelRequest(file.getAbsolutePath());
    }
}
