package com.simplelib.container;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.simplelib.holder.ImageHolder;
import com.simplelib.holder.TextHolder;
import com.simplelib.interfaces.Item;

public class SimpleItem implements Item {
    protected String id;
    protected Object arg;

    public final @NonNull ImageHolder image = ImageHolder.create();
    public final @NonNull ImageHolder subImage = ImageHolder.create();

    public final @NonNull TextHolder text = TextHolder.create();
    public final @NonNull TextHolder subText = TextHolder.create();

    public SimpleItem() {
    }

    public SimpleItem(String id) {
        this.id = id;
    }

    public SimpleItem(int imageId, int textId) {
        this.image.iconRes = imageId;
        this.text.textRes = textId;
    }

    public SimpleItem(int imageId, int subImageId, int textId) {
        this.image.iconRes = imageId;
        this.subImage.iconRes = subImageId;
        this.text.textRes = textId;
    }

    public SimpleItem(int imageId, int subImageId, int textId, int subTextId) {
        this.image.iconRes = imageId;
        this.subImage.iconRes = subImageId;
        this.text.textRes = textId;
        this.subText.textRes = subTextId;
    }

    public SimpleItem(Drawable image, String text) {
        this.image.icon = image;
        this.text.text = text;
    }

    public SimpleItem(Drawable image, Drawable subImage, String text) {
        this.image.icon = image;
        this.subImage.icon = subImage;
        this.text.text = text;
    }

    public SimpleItem(Drawable image, Drawable subImage, String text, String subText) {
        this.image.icon = image;
        this.subImage.icon = subImage;
        this.text.text = text;
        this.subText.text = subText;
    }

    public SimpleItem(Bitmap bitmap, String text) {
        this.image.bitmap = bitmap;
        this.text.text = text;
    }

    public SimpleItem(Bitmap bitmap, Bitmap subBitmap, String text) {
        this.image.bitmap = bitmap;
        this.subImage.bitmap = subBitmap;
        this.text.text = text;
    }

    public SimpleItem(Bitmap bitmap, Bitmap subBitmap, String text, String subText) {
        this.image.bitmap = bitmap;
        this.subImage.bitmap = subBitmap;
        this.text.text = text;
        this.subText.text = subText;
    }

    public SimpleItem setId(String id) {
        this.id = id;
        notifyListener();
        return this;
    }

    public SimpleItem setArg(Object arg) {
        this.arg = arg;
        notifyListener();
        return this;
    }

    public SimpleItem setImage(Drawable image) {
        this.image.icon = image;
        notifyListener();
        return this;
    }

    public SimpleItem setImageId(int imageId) {
        this.image.iconRes = imageId;
        notifyListener();
        return this;
    }

    public SimpleItem setBitmap(Bitmap bitmap) {
        this.image.bitmap = bitmap;
        notifyListener();
        return this;
    }

    public SimpleItem setImageColor(@ColorInt Integer imageColor) {
        this.image.tint.color = imageColor;
        notifyListener();
        return this;
    }

    public SimpleItem setSubImage(Drawable subImage) {
        this.subImage.icon = subImage;
        notifyListener();
        return this;
    }

    public SimpleItem setSubImageId(int subImageId) {
        this.subImage.iconRes = subImageId;
        notifyListener();
        return this;
    }

    public SimpleItem setSubBitmap(Bitmap subBitmap) {
        this.subImage.bitmap = subBitmap;
        notifyListener();
        return this;
    }

    public SimpleItem setSubImageColor(@ColorInt Integer subImageColor) {
        this.subImage.tint.color = subImageColor;
        notifyListener();
        return this;
    }

    public SimpleItem setText(String text) {
        this.text.text = text;
        notifyListener();
        return this;
    }

    public SimpleItem setTextId(int textId) {
        this.text.textRes = textId;
        notifyListener();
        return this;
    }

    public SimpleItem setSubText(String subText) {
        this.subText.text = subText;
        notifyListener();
        return this;
    }

    public SimpleItem setSubTextId(int subTextId) {
        this.subText.textRes = subTextId;
        notifyListener();
        return this;
    }

    public String getId() {
        return id;
    }

    public Object getArg() {
        return arg;
    }

    @NonNull
    @Override
    public ImageHolder getImage() {
        return image;
    }

    @NonNull
    @Override
    public ImageHolder getSubImage() {
        return subImage;
    }

    @NonNull
    @Override
    public TextHolder getText() {
        return text;
    }

    @NonNull
    @Override
    public TextHolder getSubText() {
        return subText;
    }

    @Nullable
    public Integer getImageColor() {
        return image.tint.color;
    }

    @Nullable
    public Integer getSubImageColor() {
        return subImage.tint.color;
    }

    public void notifyListener() {
    }

    public static class Inflater {
        public static final <E extends Enum<E> & SimpleItemAdapter> SimpleItem[] inflate(Class<E> enumClass) {
            if (enumClass == null)
                throw new NullPointerException("No enum attached");

            try {
                E[] constantList = enumClass.getEnumConstants();
                if (constantList != null) {
                    int constantCount = constantList.length;
                    SimpleItem[] itemList = new SimpleItem[constantCount];

                    for (int pos = 0; pos < constantCount; pos++) {
                        SimpleItemAdapter constant = constantList[pos];
                        if (constant == null) continue;

                        SimpleItem item = null;
                        try {
                            item = constant.asSimpleItem();
                            itemList[pos] = item;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (item != null) {
                            try {
                                if (item.getId() == null) {
                                    Enum<E> enumData = constantList[pos];
                                    if (enumData != null)
                                        item.setId(enumData.name());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    return itemList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SimpleItem[0];
        }

        public interface SimpleItemAdapter {
            SimpleItem asSimpleItem();
        }
    }
}
