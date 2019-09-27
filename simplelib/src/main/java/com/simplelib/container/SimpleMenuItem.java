package com.simplelib.container;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.simplelib.tools.ImageTools;

import java.util.HashMap;

public class SimpleMenuItem {
    public static SimpleMenuItem create(String text) {
        return new SimpleMenuItem(text);
    }

    public static SimpleMenuItem create(String text, int imageId) {
        return new SimpleMenuItem(text, imageId);
    }

    public static SimpleMenuItem create(String text, Drawable drawable) {
        return new SimpleMenuItem(text, drawable);
    }

    public static SimpleMenuItem create(String text, Bitmap image) {
        return new SimpleMenuItem(text, image);
    }

    public static SimpleMenuItem create(String text, Runnable onClickListener) {
        return new SimpleMenuItem(text, onClickListener);
    }

    public static SimpleMenuItem create(String text, int imageId, Runnable onClickListener) {
        return new SimpleMenuItem(text, imageId, onClickListener);
    }

    public static SimpleMenuItem create(String text, Drawable drawable, Runnable onClickListener) {
        return new SimpleMenuItem(text, drawable, onClickListener);
    }

    public static SimpleMenuItem create(String text, Bitmap image, Runnable onClickListener) {
        return new SimpleMenuItem(text, image, onClickListener);
    }

    private String text;

    private int imageId;
    private Drawable drawable;
    private Bitmap image;

    private Runnable onClickListener;

    private HashMap<String, Object> args;

    public SimpleMenuItem(String text) {
        this.text = text;
        this.drawable = null;
        this.imageId = -1;
        this.image = null;
        init();
    }

    public SimpleMenuItem(String text, int imageId) {
        this.text = text;
        this.drawable = null;
        this.imageId = imageId;
        this.image = null;
        init();
    }

    public SimpleMenuItem(String text, Drawable drawable) {
        this.text = text;
        this.drawable = drawable;
        this.imageId = -1;
        this.image = null;
        init();
    }

    public SimpleMenuItem(String text, Bitmap image) {
        this.text = text;
        this.drawable = null;
        this.imageId = -1;
        this.image = image;
        init();
    }

    public SimpleMenuItem(String text, Runnable onClickListener) {
        this.text = text;
        this.drawable = null;
        this.imageId = -1;
        this.image = null;
        this.onClickListener = onClickListener;
        init();
    }

    public SimpleMenuItem(String text, int imageId, Runnable onClickListener) {
        this.text = text;
        this.drawable = null;
        this.imageId = imageId;
        this.image = null;
        this.onClickListener = onClickListener;
        init();
    }

    public SimpleMenuItem(String text, Drawable drawable, Runnable onClickListener) {
        this.text = text;
        this.drawable = drawable;
        this.imageId = -1;
        this.image = null;
        this.onClickListener = onClickListener;
        init();
    }

    public SimpleMenuItem(String text, Bitmap image, Runnable onClickListener) {
        this.text = text;
        this.drawable = null;
        this.imageId = -1;
        this.image = image;
        this.onClickListener = onClickListener;
        init();
    }

    private void init() {
        this.args = new HashMap<>();
    }

    public String getText() {
        return text;
    }

    public SimpleMenuItem setText(String text) {
        this.text = text;
        return this;
    }

    public boolean hasImageId() {
        return imageId != -1;
    }

    public boolean hasImageDrawable() {
        return drawable != null;
    }

    public boolean hasImage() {
        return image != null;
    }

    public int getImageId() {
        return imageId;
    }

    public SimpleMenuItem setImageId(int imageId) {
        this.imageId = imageId;
        return this;
    }

    public Drawable getImageDrawable() {
        return drawable;
    }

    public SimpleMenuItem setImageDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    public SimpleMenuItem setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    public Drawable getImage(Context context) {
        if (context == null) return null;
        try {
            if (hasImage()) {
                Drawable drawable = ImageTools.getDrawable(context, getImage());
                if (drawable != null)
                    return drawable;
            }
            if (hasImageDrawable())
                return getImageDrawable();
            if (hasImageId())
                return ContextCompat.getDrawable(context, getImageId());
        } catch (Exception e) {
        }
        return null;
    }

    public void click() {
        if (onClickListener != null)
            onClickListener.run();
    }

    public SimpleMenuItem setOnClickListener(Runnable onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public HashMap<String, Object> getArguments() {
        return args;
    }

    public void putArgument(String key, Object obj) {
        args.put(key, obj);
    }

    public Object getArgument(String key) {
        return args.get(key);
    }

    public boolean containsArgument(String key) {
        return args.containsKey(key);
    }

    public void removeArgument(String key) {
        args.remove(key);
    }
}
