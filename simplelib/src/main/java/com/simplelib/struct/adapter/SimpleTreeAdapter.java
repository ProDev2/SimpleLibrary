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
        final boolean selectable = element instanceof Selectable;
        final boolean selected = selectable && ((Selectable) element).isSelected();

        final ImageView iconView = view.findViewById(R.id.tree_item_icon);
        final ImageView subIconView = view.findViewById(R.id.tree_item_sub_icon);
        final TextView textView = view.findViewById(R.id.tree_item_text);
        final TextView subTextView = view.findViewById(R.id.tree_item_subText);

        final ImageView arrowView = view.findViewById(R.id.tree_item_arrow);

        view.setSelected(selected);

        iconView.setSelected(selected);
        subIconView.setSelected(selected);
        textView.setSelected(selected);
        subTextView.setSelected(selected);

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

        ImageHolder.applyToOrSetGone(imageHolder, iconView);
        ImageHolder.applyToOrSetGone(subImageHolder, subIconView);
        TextHolder.applyToOrHide(textHolder, textView);
        TextHolder.applyToOrHide(subTextHolder, subTextView);

        arrowView.clearAnimation();
        if (expandable && expanded)
            arrowView.setRotation(arrowRotationAngleEnd);
        else
            arrowView.setRotation(arrowRotationAngleStart);
        arrowView.setVisibility(expandable ? View.VISIBLE : View.GONE);

        ImageHolder.applyTo(arrow, arrowView);

        view.setOnClickListener(v -> {
            if (expandable) {
                final Tree.ItemGroup group = (Tree.ItemGroup) item;
                final boolean changed = group.toggleExpansion();

                if (changed) {
                    final boolean isExpanded = group.isExpanded();
                    if (isExpanded) {
                        ViewCompat.animate(arrowView)
                                .rotation(arrowRotationAngleEnd)
                                .start();
                    } else {
                        ViewCompat.animate(arrowView)
                                .rotation(arrowRotationAngleStart)
                                .start();
                    }

                    updateTreeData();
                }
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

    public void setOnClickListener(OnClickListener<E> onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener<E extends Item> {
        void onClick(Tree.Item item, @Nullable E element);
        boolean onLongClick(Tree.Item item, @Nullable E element);
    }
}
