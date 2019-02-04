package com.simplelib.holder;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;

public abstract class ViewHolder {
    private Context context;

    private View contentView;

    public ViewHolder(Context context) {
        if (context == null)
            throw new NullPointerException();

        this.context = context;

        create();
    }

    public final Context getContext() {
        return context;
    }

    public final View getContentView() {
        return contentView;
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        try {
            if (contentView != null)
                return contentView.findViewById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public View inflateLayout(@LayoutRes int id) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void recreate() {
        contentView = null;
        create();
    }

    public void create() {
        try {
            if (contentView == null)
                contentView = createHolder();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contentView == null)
            throw new NullPointerException();

        try {
            bindHolder(contentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract View createHolder();

    public abstract void bindHolder(View contentView);
}