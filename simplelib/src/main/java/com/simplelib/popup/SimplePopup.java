/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
