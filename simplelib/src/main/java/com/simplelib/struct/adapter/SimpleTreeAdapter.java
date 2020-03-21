package com.simplelib.struct.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.simplelib.R;
import com.simplelib.holder.ColorHolder;
import com.simplelib.holder.ImageHolder;
import com.simplelib.holder.TextHolder;
import com.simplelib.interfaces.Item;
import com.simplelib.interfaces.Selectable;
import com.simplelib.struct.Tree;
import com.simplelib.tools.Tools;
import com.simplelib.tools.UIUtils;

public class SimpleTreeAdapter<E extends Item> extends TreeAdapter<E> {
    private static final @ColorInt int DEFAULT_SELECTED_COLOR = 0x1F2196F3;
    private static final @ColorInt int DEFAULT_PRESSED_COLOR = 0x1F2196F3;
    private static final @AttrRes int DEFAULT_PRESSED_COLOR_RES = R.attr.colorControlHighlight;

    @IntRange(from = 0)
    public int levelOffset = Tools.dpToPx(20);

    public float arrowRotationAngleStart = 0f;
    public float arrowRotationAngleEnd = 180f;

    public final @NonNull ImageHolder arrow = ImageHolder.withIconRes(R.drawable.ic_arrow_down);

    public final @NonNull ImageHolder defaultImage = ImageHolder.create();
    public final @NonNull ImageHolder defaultSubImage = ImageHolder.create();
    public final @NonNull TextHolder defaultText = TextHolder.create();
    public final @NonNull TextHolder defaultSubText = TextHolder.create();

    public final @NonNull ColorHolder textColor = ColorHolder.create();
    public final @NonNull ColorHolder subTextColor = ColorHolder.create();

    public final @NonNull ColorHolder selectedColor = ColorHolder.create();
    public final @NonNull ColorHolder pressedColor = ColorHolder.create();

    public boolean alwaysSelectable = false;

    private OnClickListener<E> onClickListener;

    public SimpleTreeAdapter() {
    }

    public SimpleTreeAdapter(@Nullable Tree tree, boolean showRoot) {
        super(tree, showRoot);
    }

    @IntRange(from = 0)
    public int getLevelOffset() {
        return levelOffset;
    }

    public void setLevelOffset(@IntRange(from = 0) int levelOffset) {
        this.levelOffset = levelOffset;
    }

    public void setLevelOffsetInDp(@IntRange(from = 0) int levelOffsetInDp) {
        this.levelOffset = Tools.dpToPx(levelOffsetInDp);
    }

    public void setArrowRotationAngleStart(float arrowRotationAngleStart) {
        this.arrowRotationAngleStart = arrowRotationAngleStart;
    }

    public void setArrowRotationAngleEnd(float arrowRotationAngleEnd) {
        this.arrowRotationAngleEnd = arrowRotationAngleEnd;
    }

    public void setArrowRotationAngle(float start, float end) {
        this.arrowRotationAngleStart = start;
        this.arrowRotationAngleEnd = end;
    }

    public boolean isAlwaysSelectable() {
        return alwaysSelectable;
    }

    public void setAlwaysSelectable(boolean alwaysSelectable) {
        this.alwaysSelectable = alwaysSelectable;
    }

    public void setOnClickListener(OnClickListener<E> onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLevelOffset(int level) {
        if (levelOffset < 0)
            return 0;

        return level * levelOffset;
    }

    @NonNull
    @Override
    protected View createTreeView(@NonNull ViewGroup parent,
                                  int viewType) {
        return inflateLayout(parent, R.layout.simple_tree_item);
    }

    @Override
    protected void bindTreeView(final @NonNull View view,
                                final int viewType,
                                final Tree.Item item,
                                final int treeLevel,
                                final boolean expandable,
                                final boolean expanded,
                                final @Nullable E element,
                                final int pos) {
        final boolean selectable = isSelectable(element);
        final boolean selected = isSelected(element, selectable);

        final Holder holder = new Holder(view);
        holder.setSelected(selectable && selected);

        final ImageHolder imageHolder;
        final ImageHolder subImageHolder;
        final TextHolder textHolder;
        final TextHolder subTextHolder;
        if (element != null) {
            imageHolder = element.getImage();
            subImageHolder = element.getSubImage();
            textHolder = element.getText();
            subTextHolder = element.getSubText();
        } else {
            imageHolder = defaultImage;
            subImageHolder = defaultSubImage;
            textHolder = defaultText;
            subTextHolder = defaultSubText;
        }

        ImageHolder.applyToOrSetGone(imageHolder, holder.iconView);
        ImageHolder.applyToOrSetGone(subImageHolder, holder.subIconView);
        TextHolder.applyToOrHide(textHolder, holder.textView);
        TextHolder.applyToOrHide(subTextHolder, holder.subTextView);

        ColorHolder.applyTo(textColor, holder.textView);
        ColorHolder.applyTo(subTextColor, holder.subTextView);

        ImageHolder.applyToOrSetGone(arrow, holder.arrowView);

        if (holder.arrowView != null) {
            try {
                holder.arrowView.clearAnimation();
                if (expandable && expanded)
                    holder.arrowView.setRotation(arrowRotationAngleEnd);
                else
                    holder.arrowView.setRotation(arrowRotationAngleStart);
                holder.arrowView.setVisibility(expandable ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
            }
        }

        // Bind background
        bindBackground(view,
                item,
                element);

        // Bind listeners
        bindListener(view,
                viewType,
                holder,
                item,
                element);
    }

    protected void bindBackground(final @NonNull View view,
                                  final Tree.Item item,
                                  final @Nullable E element) {
        applySelectableBackground(view, false);
    }

    private void bindListener(final @NonNull View view,
                              final int viewType,
                              final @NonNull Holder holder,
                              final Tree.Item item,
                              final @Nullable E element) {
        view.setOnClickListener(v -> {
            boolean handled = false;
            boolean dataChanged = false;

            final boolean expandable = isExpandable(item);
            if (expandable) {
                final Tree.ItemGroup group = (Tree.ItemGroup) item;
                final boolean changed = group.toggleExpansion();

                if (changed && holder.arrowView != null) {
                    final boolean expanded = isExpanded(item, true);
                    try {
                        if (expanded) {
                            ViewCompat.animate(holder.arrowView)
                                    .rotation(arrowRotationAngleEnd)
                                    .start();
                        } else {
                            ViewCompat.animate(holder.arrowView)
                                    .rotation(arrowRotationAngleStart)
                                    .start();
                        }
                    } catch (Exception e) {
                    }
                }

                handled |= changed;
                dataChanged |= changed;
            }
            if (holder.arrowView != null)
                holder.arrowView.setVisibility(expandable ? View.VISIBLE : View.GONE);

            final boolean selectable = isSelectable(element);
            if (selectable && (alwaysSelectable || !handled)) {
                final Selectable selector = (Selectable) element;
                final boolean changed = selector.toggleSelection();

                if (changed) {
                    final boolean selected = isSelected(element, true);
                    holder.setSelected(selected);
                }
            } else {
                holder.setSelected(false);
            }

            if (dataChanged) {
                updateTreeData();
            }

            if (onClickListener != null)
                onClickListener.onClick(item, element);
        });

        view.setOnLongClickListener(v -> {
            if (onClickListener != null)
                return onClickListener.onLongClick(item, element);
            return true;
            //return false;
        });
    }

    protected final boolean isSelectable(E element) {
        return element instanceof Selectable && ((Selectable) element).isSelectable();
    }

    protected final boolean isSelected(E element, boolean selectable) {
        return selectable && ((Selectable) element).isSelected();
    }

    protected final void applySelectableBackground(final @NonNull View view,
                                                   final boolean legacyStyle) {
        Context context = view.getContext();
        if (context == null)
            context = getContext();

        int animTime = -1;
        try {
            animTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        } catch (Exception ignored) {
        } catch (Throwable tr) {
            tr.printStackTrace();
        }

        int selectedColor = this.selectedColor.getColor(context, DEFAULT_SELECTED_COLOR);
        int pressedColor = this.pressedColor.getColor(context, DEFAULT_PRESSED_COLOR_RES, DEFAULT_PRESSED_COLOR);

        try {
            UIUtils.applyEffect(context, view, selectedColor, pressedColor, animTime, null, legacyStyle);
        } catch (Exception ignored) {
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
    }

    public interface OnClickListener<E extends Item> {
        void onClick(Tree.Item item, @Nullable E element);
        boolean onLongClick(Tree.Item item, @Nullable E element);
    }

    private static class Holder {
        protected final View itemView;

        protected final ImageView iconView;
        protected final ImageView subIconView;
        protected final TextView textView;
        protected final TextView subTextView;

        protected final ImageView arrowView;

        public Holder(@NonNull View itemView) {
            this.itemView = itemView;

            this.iconView = itemView.findViewById(R.id.tree_item_icon);
            this.subIconView = itemView.findViewById(R.id.tree_item_sub_icon);
            this.textView = itemView.findViewById(R.id.tree_item_text);
            this.subTextView = itemView.findViewById(R.id.tree_item_sub_text);

            this.arrowView = itemView.findViewById(R.id.tree_item_arrow);
        }

        public void setSelected(boolean selected) {
            try {
                if (itemView != null) itemView.setSelected(selected);

                if (iconView != null) iconView.setSelected(selected);
                if (subIconView != null) subIconView.setSelected(selected);
                if (textView != null) textView.setSelected(selected);
                if (subTextView != null) subTextView.setSelected(selected);
            } catch (Exception e) {
            }
        }
    }
}
