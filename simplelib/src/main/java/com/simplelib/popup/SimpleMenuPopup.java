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

import android.annotation.SuppressLint;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplelib.adapter.SimpleRecyclerAdapter;
import com.simplelib.container.SimpleMenuItem;
import com.simplelib.tools.MathTools;

import java.util.ArrayList;
import java.util.List;

public class SimpleMenuPopup extends SimplePopup {
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_ITEM_DISTANCE = 0;
    public static final int DEFAULT_TEXT_COLOR = 0xFF757575;
    public static final int DEFAULT_TEXT_COLOR_HIGHLIGHTED = 0xFF03A9F4;
    public static final int DEFAULT_TEXT_SIZE = 15;
    public static final boolean DEFAULT_CLOSE_ON_CLICK = true;

    private static final int LAYOUT_MARGINS = 5;

    private List<SimpleMenuItem> list;

    private CardView card;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private Adapter adapter;

    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int itemDistance = DEFAULT_ITEM_DISTANCE;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textColorHighlighted = DEFAULT_TEXT_COLOR_HIGHLIGHTED;
    private int textSize = DEFAULT_TEXT_SIZE;
    private boolean closeOnClick = DEFAULT_CLOSE_ON_CLICK;

    public SimpleMenuPopup(View parentView) {
        this(parentView, null);
    }

    public SimpleMenuPopup(View parentView, List<SimpleMenuItem> list) {
        super(parentView);

        if (this.list == null)
            this.list = new ArrayList<>();
        if (list != null) {
            this.list.clear();
            this.list.addAll(list);
        }

        setBackgroundColor(backgroundColor);

        update();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createLayout(View parentView) {
        int margins = MathTools.dpToPx(LAYOUT_MARGINS);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        CardView card = new CardView(getContext());
        card.setRadius(margins);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(margins, margins, margins, margins);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(backgroundColor);
        card.setId(0);
        layout.addView(card);

        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams recyclerParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        recyclerParams.setMargins(margins, margins, margins, margins);
        recyclerView.setLayoutParams(recyclerParams);
        recyclerView.setId(1);
        card.addView(recyclerView);

        return layout;
    }

    @SuppressLint("ResourceType")
    @Override
    public void bindLayout(View view) {
        if (list == null)
            list = new ArrayList<>();

        card = (CardView) findViewById(0);

        recyclerView = (RecyclerView) findViewById(1);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new Adapter(list);
        recyclerView.setAdapter(adapter);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;

        if (card != null)
            card.setCardBackgroundColor(backgroundColor);
    }

    public void setItemDistance(int itemDistance) {
        this.itemDistance = itemDistance;

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void setTextColor(int color) {
        this.textColor = color;

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void setTextColorHighlighted(int color) {
        this.textColorHighlighted = color;

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void setTextSize(int size) {
        this.textSize = size;

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    public void add(SimpleMenuItem item) {
        if (adapter != null)
            adapter.add(item);
        else
            list.add(item);
    }

    public void remove(SimpleMenuItem item) {
        if (adapter != null)
            adapter.remove(item);
        else
            list.remove(item);
    }

    public void clear() {
        if (adapter != null)
            adapter.clear();
        else
            list.clear();
    }

    public List<SimpleMenuItem> getMenuItemList() {
        return list;
    }

    public void update() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public void select(String text) {
        if (adapter != null)
            adapter.select(text);
    }

    public void select(SimpleMenuItem item) {
        if (adapter != null)
            adapter.select(item);
    }

    public void unselect() {
        if (adapter != null)
            adapter.unselect();
    }

    private class Adapter extends SimpleRecyclerAdapter<SimpleMenuItem, Void> {
        private SimpleMenuItem selected;

        public Adapter() {
        }

        public Adapter(List<SimpleMenuItem> list) {
            super(list);
        }

        @NonNull
        @SuppressLint("ResourceType")
        @Override
        public View createView(@NonNull ViewGroup parent, int viewType) {
            int margins = MathTools.dpToPx(LAYOUT_MARGINS);

            int distance = itemDistance > 0 ? (MathTools.dpToPx(itemDistance) / 2) : 0;

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);
            layout.setPadding(margins, margins + distance, margins, margins + distance);
            layout.setId(0);

            int[] attrs = new int[] {com.google.android.material.R.attr.selectableItemBackgroundBorderless};
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            layout.setBackgroundResource(backgroundResource);
            typedArray.recycle();

            LinearLayout layoutSub = new LinearLayout(getContext());
            layoutSub.setOrientation(LinearLayout.HORIZONTAL);
            layoutSub.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutParamsSub = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutSub.setLayoutParams(layoutParamsSub);
            layout.addView(layoutSub);

            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.width = imageParams.height = MathTools.dpToPx(45);
            imageParams.setMargins(0, 0, margins * 2, 0);
            imageView.setLayoutParams(imageParams);
            imageView.setId(1);
            layoutSub.addView(imageView);

            TextView textView = new TextView(getContext());
            textView.setTextColor(textColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textParams);
            textView.setId(2);
            layoutSub.addView(textView);

            return layout;
        }

        @SuppressLint("ResourceType")
        @Override
        protected void bindView(@NonNull final ViewHolder holder, final SimpleMenuItem menuItem, @Nullable final Void element, final int pos) {
            LinearLayout layout = (LinearLayout) holder.findViewById(0);
            ImageView imageView = (ImageView) holder.findViewById(1);
            TextView textView = (TextView) holder.findViewById(2);

            try {
                if (menuItem.hasImage()) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(menuItem.getImage());
                } else if (menuItem.hasImageDrawable()) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(menuItem.getImageDrawable());
                } else if (menuItem.hasImageId()) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(menuItem.getImageId());
                } else {
                    imageView.setVisibility(View.GONE);
                }

                textView.setText(menuItem.getText());
            } catch (Exception e) {
            }

            try {
                int margins = MathTools.dpToPx(LAYOUT_MARGINS);

                int distance = itemDistance > 0 ? (MathTools.dpToPx(itemDistance) / 2) : 0;
                layout.setPadding(margins, margins + distance, margins, margins + distance);
            } catch (Exception e) {
            }

            try {
                if (selected != null && selected.equals(menuItem))
                    textView.setTextColor(textColorHighlighted);
                else
                    textView.setTextColor(textColor);

                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            } catch (Exception e) {
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectedBefore = getList().indexOf(selected);
                    selected = menuItem;
                    notifyItemChanged(selectedBefore);
                    notifyItemChanged(getList().indexOf(selected));

                    if (closeOnClick)
                        dismiss();

                    menuItem.click();
                }
            });
        }

        public void select(String text) {
            for (SimpleMenuItem item : getList()) {
                if (item.getText().equals(text)) {
                    select(item);
                    return;
                }
            }
        }

        public void select(SimpleMenuItem item) {
            try {
                if (getList().contains(item)) {
                    this.selected = item;

                    try {
                        if (getRecyclerView() != null)
                            notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                } else {
                    unselect();
                }
            } catch (Exception e) {
            }
        }

        public void unselect() {
            try {
                int selectedBefore = getList().indexOf(selected);
                selected = null;
                notifyItemChanged(selectedBefore);
            } catch (Exception e) {
            }
        }
    }
}