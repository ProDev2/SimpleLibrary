package com.simplelib.holder;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewHolder {
    private Context context;
    private ViewGroup parentView;

    private View contentView;

    private boolean created;
    private boolean bound;

    public ViewHolder(Context context) {
        if (context == null)
            throw new NullPointerException("No context attached");

        this.context = context;
    }

    public ViewHolder(ViewGroup parentView) {
        if (parentView == null)
            throw new NullPointerException("No parent view attached");

        this.context = parentView.getContext();

        if (context == null)
            throw new NullPointerException("No context attached");

        this.parentView = parentView;
    }

    public final Context getContext() {
        return context;
    }

    public final ViewGroup getParentView() {
        return parentView;
    }

    public final void setParentView(ViewGroup parentView) {
        this.parentView = parentView;
    }

    public final View getContentView() {
        return contentView;
    }

    public final boolean detach() {
        try {
            if (parentView != null && contentView != null) {
                int index = parentView.indexOfChild(contentView);
                if (index >= 0) {
                    parentView.removeViewAt(index);
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public final boolean attach() {
        try {
            if (parentView != null && contentView != null) {
                parentView.addView(contentView);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public final boolean attach(int index) {
        try {
            if (parentView != null && contentView != null) {
                parentView.addView(contentView, index);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public final boolean attach(int width, int height) {
        try {
            if (parentView != null && contentView != null) {
                parentView.addView(contentView, width, height);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public final boolean attach(ViewGroup.LayoutParams params) {
        try {
            if (parentView != null && contentView != null) {
                parentView.addView(contentView, params);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public final boolean attach(int index, ViewGroup.LayoutParams params) {
        try {
            if (parentView != null && contentView != null) {
                parentView.addView(contentView, index, params);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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

    public final View inflateLayout(@LayoutRes int id) {
        return inflateLayout(id, true);
    }

    public final View inflateLayout(@LayoutRes int id, boolean useParent) {
        return inflateLayout(id, useParent, false);
    }

    public final View inflateLayout(@LayoutRes int id, boolean useParent, boolean attachToParent) {
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

    public final boolean isCreated() {
        return contentView != null || created;
    }

    public final boolean isBound() {
        return bound;
    }

    public synchronized final boolean recreate() {
        destroy();
        return create();
    }

    public synchronized final boolean create() {
        return create(false);
    }

    public synchronized final boolean create(boolean rebind) {
        if (contentView == null) {
            created = false;
            bound = false;
        }

        try {
            if (contentView == null || !isCreated()) {
                contentView = createHolder(parentView);
                if (contentView != null) created = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contentView == null) {
            created = false;
            bound = false;

            return false;
        }

        try {
            if (!isBound() || rebind) {
                bindHolder(contentView);
                bound = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public synchronized final void destroy() {
        try {
            if (contentView != null)
                destroyHolder(contentView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentView = null;

        created = false;
        bound = false;
    }

    protected void destroyHolder(View contentView) {
    }

    protected abstract View createHolder(ViewGroup parentView);

    protected abstract void bindHolder(View contentView);
}