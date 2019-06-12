package com.simplelib.holder;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewHolder {
    private Context context;
    private ViewGroup parentView;

    private View contentView;

    public ViewHolder(Context context) {
        if (context == null)
            throw new NullPointerException("No context attached");

        this.context = context;

        create();
    }

    public ViewHolder(View contentView) {
        if (contentView == null)
            throw new NullPointerException("No content view attached");

        this.context = contentView.getContext();

        if (context == null)
            throw new NullPointerException("No context attached");

        this.contentView = contentView;

        create();
    }

    public ViewHolder(ViewGroup parentView) {
        if (parentView == null)
            throw new NullPointerException("No parent view attached");

        this.context = parentView.getContext();

        if (context == null)
            throw new NullPointerException("No context attached");

        this.parentView = parentView;

        create();
    }

    public final Context getContext() {
        return context;
    }

    public ViewGroup getParentView() {
        return parentView;
    }

    public void setParentView(ViewGroup parentView) {
        this.parentView = parentView;
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
        return inflateLayout(id, true);
    }

    public View inflateLayout(@LayoutRes int id, boolean useParent) {
        return inflateLayout(id, useParent, false);
    }

    public View inflateLayout(@LayoutRes int id, boolean useParent, boolean attachToParent) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (parentView != null && useParent)
                return inflater.inflate(id, parentView, attachToParent);
            else
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
                contentView = createHolder(parentView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contentView == null)
            throw new NullPointerException("No content view attached");

        try {
            if (contentView != null)
                bindHolder(contentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract View createHolder(ViewGroup parentView);

    public abstract void bindHolder(View contentView);
}