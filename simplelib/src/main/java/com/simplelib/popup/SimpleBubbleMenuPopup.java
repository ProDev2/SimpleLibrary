package com.simplelib.popup;

import android.annotation.SuppressLint;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simplelib.adapter.SimpleRecyclerFilterAdapter;
import com.simplelib.container.SimpleFilter;
import com.simplelib.container.SimpleMenuItem;
import com.simplelib.math.Line;
import com.simplelib.tools.ColorTools;
import com.simplelib.tools.MathTools;
import com.simplelib.tools.RippleTools;
import com.simplelib.views.BubbleCardView;

import java.util.ArrayList;
import java.util.List;

public class SimpleBubbleMenuPopup extends SimplePopup {
    public static final float DEFAULT_ELEVATION = MathTools.dpToPx(3);
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF;
    public static final float DEFAULT_BACKGROUND_COLOR_SELECTED_MANIPULATION = 0.8f;
    public static final float DEFAULT_BACKGROUND_CORNER_RADIUS = -1;
    public static final int DEFAULT_ITEM_OVERLAP = MathTools.dpToPx(5);
    public static final int DEFAULT_ITEM_DISTANCE = MathTools.dpToPx(2);
    public static final int DEFAULT_LAYOUT_MARGINS = MathTools.dpToPx(5);
    public static final int DEFAULT_IMAGE_MARGINS = MathTools.dpToPx(3);
    public static final int DEFAULT_TEXT_MARGINS = MathTools.dpToPx(5);
    public static final int DEFAULT_LAYOUT_IMAGE_SIZE = MathTools.dpToPx(42);
    public static final int DEFAULT_LAYOUT_TEXT_SIZE = MathTools.dpToPx(130);
    public static final ColorFilter DEFAULT_IMAGE_COLOR_FILTER = null;
    public static final int DEFAULT_TEXT_COLOR = 0xFF757575;
    public static final int DEFAULT_TEXT_COLOR_HIGHLIGHTED = 0xFF03A9F4;
    public static final int DEFAULT_TEXT_SIZE = 17;
    public static final boolean DEFAULT_CLOSE_ON_CLICK = true;

    private List<SimpleMenuItem> list;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private Adapter adapter;
    private SimpleFilter<SimpleMenuItem, Void> filter;

    private float elevation = DEFAULT_ELEVATION;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int backgroundColorSelected = DEFAULT_BACKGROUND_COLOR;
    private float backgroundColorSelectedManipulation = DEFAULT_BACKGROUND_COLOR_SELECTED_MANIPULATION;
    private float backgroundCornerRadius = DEFAULT_BACKGROUND_CORNER_RADIUS;
    private int itemOverlap = DEFAULT_ITEM_OVERLAP;
    private int itemDistance = DEFAULT_ITEM_DISTANCE;
    private int layoutMargins = DEFAULT_LAYOUT_MARGINS;
    private int imageMargins = DEFAULT_IMAGE_MARGINS;
    private int textMargins = DEFAULT_TEXT_MARGINS;
    private int layoutImageSize = DEFAULT_LAYOUT_IMAGE_SIZE;
    private int layoutTextSize = DEFAULT_LAYOUT_TEXT_SIZE;
    private ColorFilter imageColorFilter = DEFAULT_IMAGE_COLOR_FILTER;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textColorHighlighted = DEFAULT_TEXT_COLOR_HIGHLIGHTED;
    private int textSize = DEFAULT_TEXT_SIZE;
    private boolean closeOnClick = DEFAULT_CLOSE_ON_CLICK;

    public SimpleBubbleMenuPopup(View parentView) {
        this(parentView, null);
    }

    public SimpleBubbleMenuPopup(View parentView, List<SimpleMenuItem> list) {
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
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams recyclerParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        recyclerView.setLayoutParams(recyclerParams);
        recyclerView.setId(0);
        layout.addView(recyclerView);

        return layout;
    }

    @SuppressLint("ResourceType")
    @Override
    public void bindLayout(View view) {
        if (list == null)
            list = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(0);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

		/*try {
			while (recyclerView.getItemDecorationCount() > 0) {
				recyclerView.removeItemDecorationAt(0);
			}
		} catch (Exception e) {
		}*/
        try {
            recyclerView.addItemDecoration(new OverlapDecoration());
        } catch (Exception e) {
        }

        adapter = new Adapter(list);
        if (filter != null)
            adapter.setFilter(filter, false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColorSelected(int backgroundColorSelected, float backgroundColorSelectedManipulation) {
        this.backgroundColorSelected = backgroundColorSelected;
        this.backgroundColorSelectedManipulation = backgroundColorSelectedManipulation;
    }

    public void setBackgroundCornerRadius(float backgroundCornerRadius) {
        this.backgroundCornerRadius = backgroundCornerRadius;
    }

    public void setItemOverlap(int itemOverlap) {
        this.itemOverlap = itemOverlap;
    }

    public void setItemDistance(int itemDistance) {
        this.itemDistance = itemDistance;
    }

    public void setLayoutMargins(int layoutMargins) {
        this.layoutMargins = layoutMargins;
    }

    public void setImageMargins(int imageMargins) {
        this.imageMargins = imageMargins;
    }

    public void setTextMargins(int textMargins) {
        this.textMargins = textMargins;
    }

    public void setLayoutImageSize(int layoutImageSize) {
        this.layoutImageSize = layoutImageSize;
    }

    public void setLayoutTextSize(int layoutTextSize) {
        this.layoutTextSize = layoutTextSize;
    }

    public void setImageColorFilter(ColorFilter imageColorFilter) {
        this.imageColorFilter = imageColorFilter;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void setTextColorHighlighted(int color) {
        this.textColorHighlighted = color;
    }

    public void setTextSize(int size) {
        this.textSize = size;
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

    public void setFilter(SimpleFilter<SimpleMenuItem, Void> filter) {
        this.filter = filter;

        if (adapter != null)
            adapter.setFilter(filter, false);
    }

    public void update() {
        if (adapter != null)
            adapter.update();
    }

    public void reload() {
        if (adapter != null)
            adapter.reload();
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

    public void onModifyItemLayout(SimpleRecyclerFilterAdapter<SimpleMenuItem, Void>.ViewHolder holder, SimpleMenuItem menuItem, int pos) {
        if (holder == null) return;

        View itemView = holder.itemView;
        if (itemView == null) return;

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.25f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setFillAfter(true);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(300);
        itemView.startAnimation(animation);
    }

    public void onModifyBubbleCard(BubbleCardView bubbleCardView, int itemPos) {
        if (bubbleCardView == null) return;

        try {
            final View targetView = getParentView();

            if (targetView != null && manager != null) {
                final int firstVisibleItemPos = manager.findFirstVisibleItemPosition();
                final int lastVisibleItemPos = manager.findLastVisibleItemPosition();

                final boolean isFirstVisibleItem = firstVisibleItemPos >= 0 && firstVisibleItemPos >= itemPos;
                final boolean isLastVisibleItem = lastVisibleItemPos >= 0 && lastVisibleItemPos <= itemPos;

                if (isFirstVisibleItem || isLastVisibleItem) {
                    View firstView = manager.findViewByPosition(firstVisibleItemPos);
                    View lastView = manager.findViewByPosition(lastVisibleItemPos);

                    double firstDist = getDistance(targetView, firstView);
                    double lastDist = getDistance(targetView, lastView);

                    boolean isArrow = (lastDist < 0 && isFirstVisibleItem) ||
                            (firstDist < 0 && isLastVisibleItem) ||
                            (isFirstVisibleItem && firstDist <= lastDist) ||
                            (isLastVisibleItem && lastDist <= firstDist);

                    if (isArrow) {
                        if (bubbleCardView.getPointer() == null) {
                            BubbleCardView.Pointer pointer = new BubbleCardView.Pointer(targetView, false);
                            bubbleCardView.setPointer(pointer);
                        } else {
                            BubbleCardView.Pointer pointer = bubbleCardView.getPointer();
                            pointer.setPointerView(targetView, false);
                        }
                    } else {
                        if (bubbleCardView.getDrawable().getArrowTarget() != null)
                            bubbleCardView.getDrawable().setArrowTarget(null);
                    }
                }
            } else {
                if (bubbleCardView.getDrawable().getArrowTarget() != null)
                    bubbleCardView.getDrawable().setArrowTarget(null);
            }
        } catch (Exception e) {
        }
    }

    private double getDistance(View v1, View v2) {
        if (v1 == null || v2 == null)
            return -1;

        Rect view1Bounds = getViewBounds(v1);
        Rect view2Bounds = getViewBounds(v2);

        if (view1Bounds == null || view2Bounds == null)
            return -1;

        Line line = new Line(
                view1Bounds.centerX(),
                view1Bounds.centerY(),
                view2Bounds.centerX(),
                view2Bounds.centerY()
        );

        double dist = line.getLength();
        if (dist < 0) dist = -dist;
        return dist;
    }

    private Rect getViewBounds(View v) {
        if (v == null)
            return null;

        try {
            int[] pos = new int[2];
            v.getLocationOnScreen(pos);

            int width = v.getWidth();
            int height = v.getHeight();

            if (width <= 0)
                width = v.getMeasuredWidth();
            if (height <= 0)
                height = v.getMeasuredHeight();

            if (width <= 0 || height <= 0)
                return null;

            return new Rect(pos[0],
                    pos[1],
                    pos[0] + width,
                    pos[1] + height);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class OverlapDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (outRect == null) return;

			/*
			final int itemPosition = parent != null ? parent.getChildAdapterPosition(view) : 0;
			if (itemPosition == 0)
				return;
			outRect.set(0, -itemOverlap, 0, 0);
			*/

            final int itemPosition = parent != null ? parent.getChildAdapterPosition(view) : 0;
            if (itemPosition == 0)
                outRect.set(0, 0, 0, 0);
            else
                outRect.set(0, -itemOverlap * 2, 0, 0);
        }
    }

    private class Adapter extends SimpleRecyclerFilterAdapter<SimpleMenuItem, Void> {
        private SimpleMenuItem selected;

        public Adapter() {
        }

        public Adapter(List<SimpleMenuItem> list) {
            super(list);
        }

        @NonNull
        @SuppressLint("ResourceType")
        @Override
        public View createHolder(@NonNull ViewGroup parent, int viewType) {
            int margins = layoutMargins > 0 ? layoutMargins : 0;
            int marginsImage = imageMargins > 0 ? imageMargins : 0;
            int marginsText = textMargins > 0 ? textMargins : 0;
            int distance = itemDistance > 0 ? itemDistance : 0;
            int imageLayoutSize = layoutImageSize >= 0 ? layoutImageSize : ViewGroup.LayoutParams.WRAP_CONTENT;
            int textLayoutSize = layoutTextSize >= 0 ? layoutTextSize : ViewGroup.LayoutParams.WRAP_CONTENT;

            final BubbleCardView layout = new BubbleCardView(getContext());
            BubbleCardView.LayoutParams layoutParams = new BubbleCardView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, distance / 2, 0, distance / 2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.setMarginStart(0);
                layoutParams.setMarginEnd(0);
            }
            layout.setLayoutParams(layoutParams);
            layout.setId(0);

            final LinearLayout layoutSub = new LinearLayout(getContext());
            layoutSub.setOrientation(LinearLayout.VERTICAL);
            layoutSub.setGravity(Gravity.CENTER_VERTICAL);
            BubbleCardView.LayoutParams layoutParamsSub = new BubbleCardView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutSub.setLayoutParams(layoutParamsSub);
            layoutSub.setId(1);
            layout.addView(layoutSub);

            layoutSub.setClickable(true);
            layoutSub.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        setRippleBackground(layoutSub);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            final LinearLayout layoutContent = new LinearLayout(getContext());
            layoutContent.setOrientation(LinearLayout.HORIZONTAL);
            layoutContent.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutParamsContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutContent.setLayoutParams(layoutParamsContent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutContent.setPaddingRelative(margins, margins, margins, margins);
            } else {
                layoutContent.setPadding(margins, margins, margins, margins);
            }
            layoutContent.setId(2);
            layoutSub.addView(layoutContent);

            final LinearLayout layoutTextContent = new LinearLayout(getContext());
            layoutTextContent.setOrientation(LinearLayout.VERTICAL);
            layoutTextContent.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutParamsTextContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutTextContent.setLayoutParams(layoutParamsTextContent);
            layoutTextContent.setId(3);
            layoutContent.addView(layoutTextContent);

            final ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.width = imageParams.height = imageLayoutSize;
            imageParams.setMargins(marginsImage, marginsImage, marginsImage, marginsImage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                imageParams.setMarginStart(marginsImage);
                imageParams.setMarginEnd(marginsImage);
            }
            imageView.setLayoutParams(imageParams);
            imageView.setId(4);
            layoutContent.addView(imageView);

            final TextView textView = new TextView(getContext());
            textView.setTextColor(textColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.width = textLayoutSize;
            textParams.setMargins(marginsText, marginsText, marginsText, marginsText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textParams.setMarginStart(marginsText);
                textParams.setMarginEnd(marginsText);
            }
            textView.setLayoutParams(textParams);
            textView.setId(5);
            layoutContent.addView(textView);

            return layout;
        }

        @Override
        protected void bindHolder(@NonNull final ViewHolder holder, final SimpleMenuItem menuItem, @Nullable final Void element, final int pos) {
            final BubbleCardView layout = (BubbleCardView) holder.findViewById(0);
            final LinearLayout layoutSub = (LinearLayout) holder.findViewById(1);
            final LinearLayout layoutContent = (LinearLayout) holder.findViewById(2);
            final LinearLayout layoutTextContent = (LinearLayout) holder.findViewById(3);
            final ImageView imageView = (ImageView) holder.findViewById(4);
            final TextView textView = (TextView) holder.findViewById(5);

            try {
                int margins = layoutMargins > 0 ? layoutMargins : 0;
                int marginsImage = imageMargins > 0 ? imageMargins : 0;
                int marginsText = textMargins > 0 ? textMargins : 0;
                int distance = itemDistance > 0 ? itemDistance : 0;
                int imageLayoutSize = layoutImageSize >= 0 ? layoutImageSize : ViewGroup.LayoutParams.WRAP_CONTENT;
                int textLayoutSize = layoutTextSize >= 0 ? layoutTextSize : ViewGroup.LayoutParams.WRAP_CONTENT;

                BubbleCardView.LayoutParams layoutParams = (BubbleCardView.LayoutParams) layout.getLayoutParams();
                layoutParams.setMargins(0, distance / 2, 0, distance / 2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.setMarginStart(0);
                    layoutParams.setMarginEnd(0);
                }
                layout.setLayoutParams(layoutParams);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutContent.setPaddingRelative(margins, margins, margins, margins);
                } else {
                    layoutContent.setPadding(margins, margins, margins, margins);
                }

                LinearLayout.LayoutParams imageParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                imageParams.width = imageParams.height = imageLayoutSize;
                imageParams.setMargins(marginsImage, marginsImage, marginsImage, marginsImage);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    imageParams.setMarginStart(marginsImage);
                    imageParams.setMarginEnd(marginsImage);
                }
                imageView.setLayoutParams(imageParams);

                LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                textParams.width = textLayoutSize;
                textParams.setMargins(marginsText, marginsText, marginsText, marginsText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textParams.setMarginStart(marginsText);
                    textParams.setMarginEnd(marginsText);
                }
                textView.setLayoutParams(textParams);
            } catch (Exception e) {
            }

            try {
                if (imageColorFilter != null)
                    imageView.setColorFilter(imageColorFilter);
                else
                    imageView.clearColorFilter();
            } catch (Exception e) {
            }

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
                if (selected != null && selected.equals(menuItem))
                    textView.setTextColor(textColorHighlighted);
                else
                    textView.setTextColor(textColor);

                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            } catch (Exception e) {
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    float elevation = SimpleBubbleMenuPopup.this.elevation;
                    if (elevation < 0)
                        elevation = 0;
                    layout.setElevation(elevation);
                }

                layout.setColor(backgroundColor);
                layout.setCornerRadius(backgroundCornerRadius);

                layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            final int itemPos = holder.getAdapterPosition();

                            onModifyBubbleCard(layout, itemPos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                layoutSub.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            layoutSub.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            setRippleBackground(layoutSub);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
            }

            layoutSub.setOnClickListener(new View.OnClickListener() {
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

            try {
                onModifyItemLayout(holder, menuItem, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setRippleBackground(View view) {
            if (view == null) return;

            float cornerRadii = 0;
            if (backgroundCornerRadius >= 0) {
                cornerRadii = backgroundCornerRadius;
            } else {
                int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
                int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();

                cornerRadii = Math.min(width, height) / 2;
            }
            if (cornerRadii < 0) cornerRadii = 0;

            int colorNormal = backgroundColor;
            int colorSelected = ColorTools.manipulateColor(backgroundColorSelected, backgroundColorSelectedManipulation);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

            Drawable rippleDrawable = RippleTools.getAdaptiveRippleDrawable(cornerRadii, colorNormal, colorSelected);
            view.setBackground(rippleDrawable);
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
                            reload();
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