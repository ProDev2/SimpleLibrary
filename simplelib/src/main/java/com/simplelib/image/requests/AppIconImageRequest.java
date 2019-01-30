package com.simplelib.image.requests;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.simplelib.image.ImageLoader;
import com.simplelib.tools.ImageTools;

public abstract class AppIconImageRequest extends ImageLoader.ImageRequest {
    private PackageManager manager;
    private ResolveInfo info;

    private int reqWidth = -1;
    private int reqHeight = -1;

    public AppIconImageRequest(Context context, ResolveInfo info) {
        super(info.activityInfo.name);

        this.manager = context.getPackageManager();
        this.info = info;
    }

    public AppIconImageRequest(PackageManager manager, ResolveInfo info) {
        super(info.activityInfo.name);

        this.manager = manager;
        this.info = info;
    }

    public AppIconImageRequest(Context context, ResolveInfo info, int reqWidth, int reqHeight) {
        super(info.activityInfo.name);

        this.manager = context.getPackageManager();
        this.info = info;

        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public AppIconImageRequest(PackageManager manager, ResolveInfo info, int reqWidth, int reqHeight) {
        super(info.activityInfo.name);

        this.manager = manager;
        this.info = info;

        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    public Bitmap onLoad() {
        if (manager != null && info != null) {
            Drawable drawable = info.loadIcon(manager);
            if (drawable != null) {
                if (reqWidth > 0 && reqHeight > 0)
                    return ImageTools.getBitmap(drawable, reqWidth, reqHeight);
                else
                    return ImageTools.getBitmap(drawable);
            }
        }
        return null;
    }

    @Override
    public void applyTo(ImageLoader.ImageRequest request) {
        super.applyTo(request);

        if (request instanceof AppIconImageRequest) {
            AppIconImageRequest appIconImageRequest = (AppIconImageRequest) request;

            appIconImageRequest.manager = this.manager;
            appIconImageRequest.info = this.info;
        }
    }

    public static void cancelRequest(ImageLoader loader, ResolveInfo info) {
        if (loader != null && info != null && info.activityInfo != null)
            loader.cancelRequest(info.activityInfo.name);
    }
}
