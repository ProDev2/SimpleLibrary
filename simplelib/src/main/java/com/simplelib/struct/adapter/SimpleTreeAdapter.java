package com.simplelib.struct.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.simplelib.R;
import com.simplelib.holder.ImageHolder;
import com.simplelib.holder.TextHolder;
import com.simplelib.interfaces.Item;
import com.simplelib.interfaces.Selectable;
import com.simplelib.struct.Tree;
import com.simplelib.tools.Tools;

public class SimpleTreeAdapter<E extends Item> extends TreeAdapter<E> {
    @IntRange(from = 0)
    public int levelOffset = Tools.dpToPx(20);

    public float arrowRotationAngleStart = 0f;
    public float arrowRotationAngleEnd = 180f;

    public final @NonNull ImageHolder arrow = ImageHolder.withIconRes(R.drawable.ic_arrow_down);

    public final @NonNull ImageHolder defaultImage = ImageHolder.create();
    public final @NonNull ImageHolder defaultSubImage = ImageHolder.create();
    public final @NonNull TextHolder defaultText = TextHolder.create();
    public final @NonNull TextHolder defaultSubText = TextHolder.create();

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
        return inflateLayout(parent, R.layout.tree_item);
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

        ImageHolder.applyTo(arrow, holder.arrowView);

        // Bind listeners
        bindListener(view,
                viewType,
                holder,
                item,
                element);
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

        view.setOnLongClickListener((View.OnLongClickListener) v -> {
            if (onClickListener != null)
                return onClickListener.onLongClick(item, element);
            return true;
            //return false;
        });
    }

    private final boolean isSelectable(E element) {
        return element instanceof Selectable && ((Selectable) element).isSelectable();
    }

    private final boolean isSelected(E element, boolean selectable) {
        return selectable && ((Selectable) element).isSelected();
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
            this.subTextView = itemView.findViewById(R.id.tree_item_subText);

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
