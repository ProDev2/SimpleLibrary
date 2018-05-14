package com.simplelib.container;

import android.graphics.Bitmap;

public class SimpleMenuItem {
    private String text;
    private Bitmap image;

    private Runnable onClickListener;

    public SimpleMenuItem(String text) {
        this.text = text;
    }

    public SimpleMenuItem(String text, Bitmap image) {
        this.text = text;
        this.image = image;
    }

    public SimpleMenuItem(String text, Runnable onClickListener) {
        this.text = text;
        this.onClickListener = onClickListener;
    }

    public SimpleMenuItem(String text, Bitmap image, Runnable onClickListener) {
        this.text = text;
        this.image = image;
        this.onClickListener = onClickListener;
    }

    public static SimpleMenuItem create(String text) {
        return new SimpleMenuItem(text);
    }

    public static SimpleMenuItem create(String text, Bitmap image) {
        return new SimpleMenuItem(text, image);
    }

    public static SimpleMenuItem create(String text, Runnable onClickListener) {
        return new SimpleMenuItem(text, onClickListener);
    }

    public static SimpleMenuItem create(String text, Bitmap image, Runnable onClickListener) {
        return new SimpleMenuItem(text, image, onClickListener);
    }

    public String getText() {
        return text;
    }

    public SimpleMenuItem setText(String text) {
        this.text = text;
        return this;
    }

    public boolean hasImage() {
        return image != null;
    }

    public Bitmap getImage() {
        return image;
    }

    public SimpleMenuItem setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    public void click() {
        if (onClickListener != null)
            onClickListener.run();
    }

    public SimpleMenuItem setOnClickListener(Runnable onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }
}
