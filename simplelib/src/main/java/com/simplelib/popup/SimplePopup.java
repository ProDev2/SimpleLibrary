package com.simplelib.popup;

import android.content.Context;
import androidx.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public abstract class SimplePopup extends PopupWindow {
    private Context context;
    private View view;
    private View parentView;

    public SimplePopup(View parentView) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.context = parentView.getContext();
        this.parentView = parentView;
        this.view = createLayout(parentView);

        bindLayout(view);

        setContentView(view);
        setOutsideTouchable(true);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return view.findViewById(id);
    }

    public View inflateLayout(int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(layoutId, null);
    }

    public Context getContext() {
        return context;
    }

    public View getParentView() {
        return parentView;
    }

    public void showAsDropDown() {
        showAsDropDown(parentView);
    }

    public abstract View createLayout(View parentView);

    public abstract void bindLayout(View view);
}
