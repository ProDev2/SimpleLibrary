package com.simplelib.struct.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.simplelib.adapter.SimpleRecyclerFilterAdapter;
import com.simplelib.container.SimpleFilter;
import com.simplelib.struct.Tree;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeAdapter<E> extends SimpleRecyclerFilterAdapter<Tree.Item, E> {
    private Tree tree;
    private boolean showRoot;

    private SimpleFilter<Tree.Item, E> filter;

    private final SimpleFilter<Tree.Item, E> treeFilter = new SimpleFilter<Tree.Item, E>() {
        @Override
        public boolean filter(Tree.Item item) {
            if (item == null)
                return filter == null || filter.filter(null);

            if (!showRoot && item.isRoot())
                return false;

            Tree.ItemGroup parent = item.getParent();
            if (parent != null && !parent.isExpanded() && (!parent.isRoot() || showRoot))
                return false;

            return filter == null || filter.filter(item);
        }
    };

    public TreeAdapter() {
        setShowRoot(false, false);
        setTree(null, false);

        setup();
    }

    public TreeAdapter(@Nullable Tree tree, boolean showRoot) {
        setShowRoot(showRoot, false);
        setTree(tree, false);

        setup();
    }

    private void setup() {
        super.setFilter(treeFilter, false);
    }

    @Nullable
    public synchronized Tree getTree() {
        return tree;
    }

    public synchronized void setTree(@Nullable Tree tree) {
        setTree(tree, true);
    }

    public synchronized void setTree(@Nullable Tree tree, boolean update) {
        this.tree = tree;

        moveTreeIntoList();

        if (update)
            reload();
    }

    public synchronized boolean isShowRoot() {
        return showRoot;
    }

    public synchronized void setShowRoot(boolean showRoot) {
        setShowRoot(showRoot, true);
    }

    public synchronized void setShowRoot(boolean showRoot, boolean update) {
        boolean changed = this.showRoot != showRoot;
        this.showRoot = showRoot;

        if (changed && update)
            super.reload();
    }

    @Override
    public SimpleFilter<Tree.Item, E> getFilter() {
        return filter;
    }

    @Override
    public void setFilter(SimpleFilter<Tree.Item, E> filter) {
        setFilter(filter, true);
    }

    @Override
    public void setFilter(SimpleFilter<Tree.Item, E> filter, boolean update) {
        if (this.filter == filter)
            return;

        try {
            if (this.filter != null)
                this.filter.setAdapter(null);
        } catch (Exception e) {
        }

        this.filter = filter;

        try {
            if (this.filter != null)
                this.filter.setAdapter(this);
        } catch (Exception e) {
        }

        if (update)
            update();
    }

    @NonNull
    @Override
    protected final View createHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (context == null)
            context = getContext();

        final FrameLayout layout = new FrameLayout(context);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setLayoutParams(params);

        final View view = createTreeView(layout, viewType);
        if (view == null)
            throw new IllegalStateException("Tree view cannot be null");

        final ViewGroup.LayoutParams viewParams = view.getLayoutParams();
        if (viewParams != null) {
            params.width = viewParams.width;
            params.height = viewParams.height;
            layout.setLayoutParams(params);
        }

        layout.addView(view);

        return layout;
    }

    @Override
    protected final void bindHolder(@NonNull ViewHolder holder, Tree.Item item, @Nullable E element, int pos) {
        final int treeLevel = item != null ? Math.max(showRoot ? item.getLevel() : item.getLevel() - 1, 0) : 0;

        final boolean expandable = isExpandable(item);
        final boolean expanded = isExpanded(item, expandable);

        final int viewType = holder.getItemViewType();

        final View view = holder.itemView;
        if (view == null)
            return;

        if (!(view instanceof ViewGroup)) {
            bindTreeView(view, viewType, item, treeLevel, expandable, expanded, element, pos);
            return;
        }

        int levelOffset = getLevelOffset(treeLevel);
        if (levelOffset < 0)
            levelOffset = 0;
        ViewCompat.setPaddingRelative(view, levelOffset, 0, 0, 0);

        final View treeView = ((ViewGroup) view).getChildAt(0);
        bindTreeView(treeView, viewType, item, treeLevel, expandable, expanded, element, pos);
    }

    protected final boolean isExpandable(Tree.Item item) {
        return item instanceof Tree.ItemGroup && ((Tree.ItemGroup) item).getChildCount() > 0;
    }

    protected final boolean isExpanded(Tree.Item item, boolean expandable) {
        return expandable && ((Tree.ItemGroup) item).isExpanded();
    }

    @IntRange(from = 0)
    protected abstract int getLevelOffset(@IntRange(from = 0) int level);

    @NonNull
    protected abstract View createTreeView(@NonNull ViewGroup parent,
                                           int viewType);

    protected abstract void bindTreeView(@NonNull View view,
                                         int viewType,
                                         Tree.Item item,
                                         int treeLevel,
                                         boolean expandable,
                                         boolean expanded,
                                         @Nullable E element,
                                         int pos);

    public synchronized void reloadTreeData() {
        super.reload();
    }

    @Override
    public synchronized void reload() {
        moveTreeIntoList();

        super.reload();
    }

    public synchronized void updateTreeData() {
        super.update();
    }

    @Override
    public synchronized void update() {
        moveTreeIntoList();

        super.update();
    }

    public synchronized void moveTreeIntoList() {
        List<Tree.Item> list = getList();
        if (list == null) {
            list = new ArrayList<>();
            setList(list, false);
        }

        synchronized (list) {
            list.clear();

            if (tree == null)
                return;

            Tree.ItemGroup rootGroup = tree.getRoot();
            if (rootGroup == null)
                return;

            list.add(rootGroup);

            addChildItemsToList(rootGroup, list);
        }
    }

    private void addChildItemsToList(@NonNull Tree.ItemManager itemManager, @NonNull List<Tree.Item> list) {
        synchronized (itemManager) {
            int childCount = itemManager.getChildCount();
            for (int pos = 0; pos < childCount; pos++) {
                Tree.Item child = itemManager.getChildAt(pos);
                list.add(child);

                if (child instanceof Tree.ItemManager)
                    addChildItemsToList((Tree.ItemManager) child, list);
            }
        }
    }
}
