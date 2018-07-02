package com.simplelib.container;

import android.graphics.Bitmap;

public class SimpleMenuItem {
    public static SimpleMenuItem create(String text) {
        return new SimpleMenuItem(text);
    }

    public static SimpleMenuItem create(String text, int imageId) {
        return new SimpleMenuItem(text, imageId);
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

    public static SimpleMenuItem create(String text, Bitmap image, Runnable onClickListener) {
        return new SimpleMenuItem(text, image, onClickListener);
    }

    private String text;

    private int imageId;
    private Bitmap image;

    private Runnable onClickListener;

    public SimpleMenuItem(String text) {
        this.text = text;
    }

    public SimpleMenuItem(String text, int imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public SimpleMenuItem(String text, Bitmap image) {
        this.text = text;
        this.imageId = -1;
        this.image = image;
    }

    public SimpleMenuItem(String text, Runnable onClickListener) {
        this.text = text;
        this.onClickListener = onClickListener;
    }

    public SimpleMenuItem(String text, int imageId, Runnable onClickListener) {
        this.text = text;
        this.imageId = imageId;
        this.onClickListener = onClickListener;
    }

    public SimpleMenuItem(String text, Bitmap image, Runnable onClickListener) {
        this.text = text;
        this.imageId = -1;
        this.image = image;
        this.onClickListener = onClickListener;
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
