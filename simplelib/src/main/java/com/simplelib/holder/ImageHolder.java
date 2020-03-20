package com.simplelib.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

public final class ImageHolder {
    // Initialization
    @NonNull
    public static ImageHolder create() {
        return new ImageHolder();
    }

    @NonNull
    public static ImageHolder of(ImageHolder src) {
        return new ImageHolder(src);
    }

    @NonNull
    public static ImageHolder withUrl(String url) {
        return new ImageHolder(url);
    }

    @NonNull
    public static ImageHolder withUri(Uri uri) {
        return new ImageHolder(uri);
    }

    @NonNull
    public static ImageHolder withIcon(Drawable icon) {
        return new ImageHolder(icon);
    }

    @NonNull
    public static ImageHolder withBitmap(Bitmap bitmap) {
        return new ImageHolder(bitmap);
    }

    @NonNull
    public static ImageHolder withIconRes(@DrawableRes int iconRes) {
        return new ImageHolder(iconRes);
    }

    // Holder
    public @Nullable Uri uri;
    public @Nullable Drawable icon;
    public @Nullable Bitmap bitmap;
    public @Nullable @DrawableRes Integer iconRes;

    public final @NonNull ColorHolder tint = ColorHolder.create();

    public ImageHolder() {
    }

    public ImageHolder(ImageHolder src) {
        if (src != null)
            src.applyTo(this);
    }

    public ImageHolder(String url) {
        this.uri = Uri.parse(url);
    }

    public ImageHolder(Uri uri) {
        this.uri = uri;
    }

    public ImageHolder(Drawable icon) {
        this.icon = icon;
    }

    public ImageHolder(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImageHolder(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
    }

    public void applyTo(ImageHolder target) {
        if (target == null)
            return;

        target.uri = uri;
        target.icon = icon;
        target.bitmap = bitmap;
        target.iconRes = iconRes;

        tint.applyTo(target.tint);
    }

    public boolean hasImage() {
        return this.uri != null ||
                this.icon != null ||
                this.bitmap != null ||
                this.iconRes != null;
    }

    @NonNull
    public ImageHolder clear() {
        clear(true);
        return this;
    }

    @NonNull
    public ImageHolder clear(boolean recycle) {
        if (recycle && this.bitmap != null) {
            try {
                this.bitmap.recycle();
                this.bitmap = null;
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }

        this.uri = null;
        this.icon = null;
        this.bitmap = null;
        this.iconRes = null;

        this.tint.clear();
        return this;
    }

    @NonNull
    public ImageHolder setUrl(@Nullable String url) {
        try {
            this.uri = url != null ? Uri.parse(url) : null;
        } catch (Exception e) {
            this.uri = null;
        }
        return this;
    }

    @NonNull
    public ImageHolder setUri(@Nullable Uri uri) {
        this.uri = uri;
        return this;
    }

    @NonNull
    public ImageHolder setIcon(@Nullable Drawable icon) {
        this.icon = icon;
        return this;
    }

    @NonNull
    public ImageHolder setBitmap(@Nullable Bitmap bitmap) {
        setBitmap(bitmap, true);
        return this;
    }

    @NonNull
    public ImageHolder setBitmap(@Nullable Bitmap bitmap, boolean recycle) {
        if (recycle && this.bitmap != null && this.bitmap != bitmap) {
            try {
                this.bitmap.recycle();
                this.bitmap = null;
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
        this.bitmap = bitmap;
        return this;
    }

    @NonNull
    public ImageHolder setIconRes(@Nullable @DrawableRes Integer iconRes) {
        this.iconRes = iconRes;
        return this;
    }

    public ImageHolder clearTint() {
        this.tint.clear();
        return this;
    }

    @NonNull
    public ImageHolder setTint(@Nullable @ColorInt Integer color) {
        this.tint.color = color;
        return this;
    }

    @NonNull
    public ImageHolder setTintRes(@Nullable @ColorRes Integer colorRes) {
        this.tint.colorRes = colorRes;
        return this;
    }

    @NonNull
    public ImageHolder setTint(boolean tint, @ColorInt int color) {
        if (tint) {
            this.tint.color = color;
        } else {
            this.tint.color = null;
        }
        return this;
    }

    @NonNull
    public ImageHolder setTintRes(boolean tint, @ColorRes int colorRes) {
        if (tint) {
            this.tint.colorRes = colorRes;
        } else {
            this.tint.colorRes = null;
        }
        return this;
    }

    /**
     * this only handles Drawables
     *
     * @param context
     * @return
     */
    @Nullable
    public Drawable getIcon(Context context) {
        Drawable icon = this.icon;

        if (iconRes != null && context != null) {
            try {
                icon = ContextCompat.getDrawable(context, iconRes);
            } catch (Exception e) {
            }
        } else if (uri != null && context != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                icon = Drawable.createFromStream(inputStream, uri.toString());
            } catch (Exception e) {
            }
        }

        Integer color;
        if (icon != null && tint != null && (color = tint.getColor(context)) != null) {
            icon = icon.mutate();
            icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        return icon;
    }

    /**
     * sets an existing image to the imageView
     *
     * @param imageView
     * @return true if an image was set
     */
    public boolean applyTo(ImageView imageView) {
        if (imageView == null)
            return false;

        if (uri != null) {
            imageView.setImageURI(uri);
        } else if (icon != null) {
            imageView.setImageDrawable(icon);
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (iconRes != null) {
            imageView.setImageResource(iconRes);
        } else {
            imageView.setImageBitmap(null);
            return false;
        }

        Integer color;
        if (tint != null && (color = tint.getColor(imageView.getContext())) != null) {
            imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } else {
            imageView.clearColorFilter();
        }
        return true;
    }

    /**
     * a small static helper which catches nulls for us
     *
     * @param imageHolder
     * @param context
     * @return
     */
    public static Drawable getIcon(ImageHolder imageHolder, Context context) {
        return imageHolder != null ? imageHolder.getIcon(context) : null;
    }

    /**
     * a small static helper to set the image from the imageHolder nullSave to the imageView
     *
     * @param imageHolder
     * @param imageView
     * @return true if an image was set
     */
    public static boolean applyTo(ImageHolder imageHolder, ImageView imageView) {
        return imageHolder != null && imageHolder.applyTo(imageView);
    }

    /**
     * a small static helper to set the image from the imageHolder nullSave to the imageView and hide the view if no image was set
     *
     * @param imageHolder
     * @param imageView
     */
    public static void applyToOrSetInvisible(ImageHolder imageHolder, ImageView imageView) {
        boolean imageSet = applyTo(imageHolder, imageView);
        if (imageView != null) {
            if (imageSet) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * a small static helper to set the image from the imageHolder nullSave to the imageView and hide the view if no image was set
     *
     * @param imageHolder
     * @param imageView
     */
    public static void applyToOrSetGone(ImageHolder imageHolder, ImageView imageView) {
        boolean imageSet = applyTo(imageHolder, imageView);
        if (imageView != null) {
            if (imageSet) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * decides which icon to apply or hide this view
     *
     * @param imageHolder
     * @param imageView
     */
    public static void applyDecidedIconOrSetGone(ImageHolder imageHolder, ImageView imageView) {
        if (imageHolder != null && imageView != null) {
            Drawable drawable = getIcon(imageHolder, imageView.getContext());
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.VISIBLE);
            } else if (imageHolder.bitmap != null) {
                imageView.setImageBitmap(imageHolder.bitmap);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        } else if (imageView != null) {
            imageView.setVisibility(View.GONE);
        }
    }
}
