package com.simplelib.container;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.simplelib.interfaces.Selectable;

public class SimpleCheckItem extends SimpleItem implements Selectable {
    protected CheckListener checkListener;
    protected CheckListener backedCheckListener;

    protected boolean checked;

    public SimpleCheckItem() {
    }

    public SimpleCheckItem(String id) {
        super(id);
    }

    public SimpleCheckItem(int imageId, int textId) {
        super(imageId, textId);
    }

    public SimpleCheckItem(int imageId, int subImageId, int textId) {
        super(imageId, subImageId, textId);
    }

    public SimpleCheckItem(int imageId, int subImageId, int textId, int subTextId) {
        super(imageId, subImageId, textId, subTextId);
    }

    public SimpleCheckItem(int imageId, int textId, CheckListener checkListener) {
        super(imageId, textId);

        this.checkListener = checkListener;
    }

    public SimpleCheckItem(int imageId, int subImageId, int textId, CheckListener checkListener) {
        super(imageId, subImageId, textId);

        this.checkListener = checkListener;
    }

    public SimpleCheckItem(Drawable image, String text) {
        super(image, text);
    }

    public SimpleCheckItem(Drawable image, Drawable subImage, String text) {
        super(image, subImage, text);
    }

    public SimpleCheckItem(Drawable image, Drawable subImage, String text, String subText) {
        super(image, subImage, text, subText);
    }

    public SimpleCheckItem(Drawable image, String text, CheckListener checkListener) {
        super(image, text);

        this.checkListener = checkListener;
    }

    public SimpleCheckItem(Drawable image, Drawable subImage, String text, CheckListener checkListener) {
        super(image, subImage, text);

        this.checkListener = checkListener;
    }

    public SimpleCheckItem(Bitmap bitmap, String text) {
        super(bitmap, text);
    }

    public SimpleCheckItem(Bitmap bitmap, Bitmap subBitmap, String text) {
        super(bitmap, subBitmap, text);
    }

    public SimpleCheckItem(Bitmap bitmap, Bitmap subBitmap, String text, String subText) {
        super(bitmap, subBitmap, text, subText);
    }

    public SimpleCheckItem(Bitmap bitmap, String text, CheckListener checkListener) {
        super(bitmap, text);

        this.checkListener = checkListener;
    }

    public SimpleCheckItem(Bitmap bitmap, Bitmap subBitmap, String text, CheckListener checkListener) {
        super(bitmap, subBitmap, text);

        this.checkListener = checkListener;
    }

    public CheckListener getCheckListener() {
        return checkListener;
    }

    public SimpleCheckItem setCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean setChecked(boolean checked, boolean notify) {
        boolean changed = this.checked != checked;

        boolean changeable = true;
        if ((changed || notify) && checkListener != null) {
            try {
                if (checked) {
                    changeable &= checkListener.isSelectable(this);
                    changeable &= backedCheckListener.isSelectable(this);
                } else {
                    changeable &= checkListener.isUnselectable(this);
                    changeable &= backedCheckListener.isUnselectable(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                changeable = false;
            }
        }

        if ((changed || notify) && changeable) {
            this.checked = checked;
            notifyListener();
        }

        return changeable;
    }

    @Override
    public void notifyListener() {
        super.notifyListener();

        try {
            if (checkListener != null)
                checkListener.onChanged(this);
            if (backedCheckListener != null)
                backedCheckListener.onChanged(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSelected() {
        return isChecked();
    }

    @Override
    public void setSelected(boolean selected) {
        setChecked(selected, true);
    }

    public static final SimpleItem setBackedCheckListener(SimpleItem item, CheckListener backedCheckListener) {
        if (item instanceof SimpleCheckItem)
            ((SimpleCheckItem) item).backedCheckListener = backedCheckListener;
        return item;
    }

    public static final SimpleCheckItem setBackedCheckListener(SimpleCheckItem item, CheckListener backedCheckListener) {
        if (item != null)
            item.backedCheckListener = backedCheckListener;
        return item;
    }

    public static class CheckListener {
        public boolean isSelectable(SimpleCheckItem item) {
            return true;
        }

        public boolean isUnselectable(SimpleCheckItem item) {
            return true;
        }

        public void onChanged(SimpleCheckItem item) {
        }
    }

    public static class Inflater {
        public static final <E extends Enum<E> & SimpleCheckItemAdapter> SimpleCheckItem[] inflate(Class<E> enumClass) {
            if (enumClass == null)
                throw new NullPointerException("No enum attached");

            try {
                E[] constantList = enumClass.getEnumConstants();
                if (constantList != null) {
                    int constantCount = constantList.length;
                    SimpleCheckItem[] itemList = new SimpleCheckItem[constantCount];

                    for (int pos = 0; pos < constantCount; pos++) {
                        SimpleCheckItemAdapter constant = constantList[pos];
                        if (constant == null) continue;

                        SimpleCheckItem item = null;
                        try {
                            item = constant.asSimpleCheckItem();
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
            return new SimpleCheckItem[0];
        }

        public interface SimpleCheckItemAdapter {
            SimpleCheckItem asSimpleCheckItem();
        }
    }
}
