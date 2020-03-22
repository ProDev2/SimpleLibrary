package com.simplelib.struct;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class Tree {
    // Static variables
    public static final String ROOT_ID = "ROOT";

    // Static methods
    @NonNull
    public static final <V, E extends Enum<E> & ItemCreator<V>> Tree with(@Nullable V value, Class<E> enumClass) {
        final Tree tree = new Tree();

        E[] constantList = enumClass.getEnumConstants();
        if (constantList != null) {
            int constantCount = constantList.length;

            ItemGroup curGroup = tree.getRoot();
            for (int pos = 0; pos < constantCount; pos++) {
                ItemCreator<V> itemCreator = constantList[pos];
                if (itemCreator == null)
                    continue;

                int curGroupLevel;
                if (curGroup == null || (curGroupLevel = curGroup.getLevel()) < 0)
                    throw new IllegalStateException("Something went wrong");

                Integer level = itemCreator.getLevel(value);
                if (level == null)
                    continue;
                if (level <= 0 || level > curGroupLevel + 1)
                    throw new IllegalStateException("Item cannot be placed at level " + level);

                while (curGroup != null &&
                        (curGroupLevel = curGroup.getLevel()) != level - 1)
                    curGroup = curGroup.getParent();

                if (curGroup == null)
                    throw new IllegalStateException("No group found at level" + (level - 1));
                if (curGroupLevel < 0 || curGroupLevel + 1 < level)
                    throw new IllegalStateException("Something went wrong");

                // Create item and set its level
                Item item = itemCreator.create(value);
                if (item == null)
                    throw new NullPointerException("Item creator returned null");

                item.setLevel(level);

                // Attach newly created item
                curGroup.attachChild(item);

                // Set current group to item, if item is instanceof ItemGroup
                if (item instanceof ItemGroup)
                    curGroup = (ItemGroup) item;
            }
        }

        return tree;
    }

    // Tree initialization adapter
    public interface ItemCreator<V> {
        @Nullable
        @IntRange(from = 1)
        Integer getLevel(@Nullable V value);

        @NonNull
        Item create(@Nullable V value);
    }

    // Tree class
    @NonNull
    public final ItemGroup root;

    public Tree() {
        this.root = new IRootItem();
    }

    public Tree(@Nullable List<Item> itemList) {
        this.root = new IRootItem(itemList);
    }

    @NonNull
    public ItemGroup getRoot() {
        return root;
    }

    public void clear() {
        root.detachAll();
    }

    public <T> T findItemById(@Nullable String id) {
        return root.findItemById(id);
    }

    public static class IItemWrapper<V> extends IItem {
        @NonNull
        protected final V value;

        public IItemWrapper(@NonNull V value) {
            if (value == null)
                throw new NullPointerException("No value attached");
            this.value = value;
        }

        @NonNull
        public final V getValue() {
            return value;
        }
    }

    public static class IItemGroupWrapper<V> extends IItemGroup {
        @NonNull
        protected final V value;

        public IItemGroupWrapper(@NonNull V value) {
            if (value == null)
                throw new NullPointerException("No value attached");
            this.value = value;
        }

        @NonNull
        public final V getValue() {
            return value;
        }
    }

    public static class IItem implements Item {
        protected String id;
        protected int level;

        protected ItemGroup parent;

        protected boolean selectable;
        protected boolean selected;

        @Nullable
        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(@Nullable String id) {
            this.id = id;
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public void setLevel(int level) {
            if (level < 0)
                throw new IllegalArgumentException("Level cannot be smaller than 0");

            this.level = level;
        }

        @Override
        public boolean isSelectable() {
            return selectable;
        }

        protected void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            if (!selectable)
                throw new IllegalStateException("Selection state cannot be changed");

            this.selected = selected;
        }

        @Nullable
        @Override
        public ItemGroup getParent() {
            return parent;
        }

        @Override
        public void setParent(@Nullable ItemGroup parent) {
            if (this.parent != null && parent != null) {
                boolean detached = detachSelf();
                if (!detached || this.parent != null)
                    throw new IllegalStateException("Previous parent could not be detached");
            }

            this.parent = parent;
        }
    }

    public static class IItemGroup extends IItem implements ItemGroup {
        @NonNull
        protected final List<Item> childList;

        protected boolean expanded;

        public IItemGroup() {
            this(null);
        }

        public IItemGroup(@Nullable List<Item> childList) {
            if (childList == null)
                childList = new ArrayList<>();
            this.childList = childList;

            this.expanded = false;
        }

        @Override
        public void detachAll() {
            synchronized (this) {
                synchronized (childList) {
                    for (Item child : childList) {
                        if (child == null)
                            continue;

                        child.setParent(null);
                    }

                    childList.clear();
                }
            }
        }

        @Override
        public void attachChild(int index, @Nullable Item child) {
            synchronized (this) {
                synchronized (childList) {
                    childList.add(index, child);

                    if (child != null)
                        child.setParent(this);
                }
            }
        }

        @Override
        public void detachChild(int index) {
            synchronized (this) {
                synchronized (childList) {
                    Item child = childList.remove(index);

                    if (child != null)
                        child.setParent(null);
                }
            }
        }

        @Override
        public int getChildCount() {
            synchronized (this) {
                return childList.size();
            }
        }

        @Override
        public Item getChildAt(int index) {
            synchronized (this) {
                return childList.get(index);
            }
        }

        @Override
        public boolean isExpanded() {
            return expanded;
        }

        @Override
        public boolean setExpanded(boolean expanded) {
            if (expanded && !isExpandable())
                expanded = false;

            boolean changed = this.expanded != expanded;
            this.expanded = expanded;
            if (changed && !expanded)
                setChildItemsExpanded(false);
            return changed;
        }

        protected boolean isExpandable() {
            return true;
        }
    }

    public static final class IRootItem extends IItemGroup {
        public IRootItem() {
        }

        public IRootItem(@Nullable List<Item> childList) {
            super(childList);
        }

        @Override
        public boolean isRoot() {
            return true;
        }

        @Override
        public String getId() {
            return ROOT_ID;
        }

        @Override
        public void setId(@Nullable String id) {
            throw new RuntimeException("Root id cannot be changed");
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public void setLevel(int level) {
            throw new RuntimeException("Root level cannot be changed");
        }

        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public void setSelected(boolean selected) {
            if (selected)
                throw new RuntimeException("Root cannot is not selectable");
        }

        @Nullable
        @Override
        public ItemGroup getParent() {
            return null;
        }

        @Override
        public void setParent(@Nullable ItemGroup parent) {
            throw new RuntimeException("Root parent cannot be changed");
        }

        @Override
        public void attachChild(int index, @Nullable Item child) {
            super.attachChild(index, child);
        }

        @Override
        public void detachChild(int index) {
            super.detachChild(index);
        }
    }

    public interface Item {
        default boolean isRoot() {
            return false;
        }

        @Nullable
        String getId();

        void setId(@Nullable String id);

        @IntRange(from = 0)
        int getLevel();

        void setLevel(@IntRange(from = 0) int level);

        default boolean isSelectable() {
            return true;
        }

        boolean isSelected();

        void setSelected(boolean selected);

        @Nullable
        default Item getRoot() {
            Item item = this;
            Item parent;
            while (!item.isRoot() && (parent = item.getParent()) != null)
                item = parent;
            return item;
        }

        @Nullable
        ItemGroup getParent();

        void setParent(@Nullable ItemGroup parent);

        @SuppressWarnings({"PointlessNullCheck", "unchecked"})
        @Nullable
        default <T> T findItemById(@Nullable String id) {
            String mId = getId();
            if (id != null && mId != null && id.equals(mId))
                return (T) this;

            return null;
        }

        default boolean detachSelf() {
            ItemGroup parent = getParent();
            if (parent == null)
                return false;
            return parent.detachChild(this);
        }
    }

    public interface ItemGroup extends Item, ItemManager {
        @SuppressWarnings({"PointlessNullCheck", "unchecked"})
        @Nullable
        @Override
        default <T> T findItemById(@Nullable String id) {
            String mId = getId();
            if (id != null && mId != null && id.equals(mId))
                return (T) this;

            synchronized (this) {
                int childCount = getChildCount();
                for (int pos = 0; pos < childCount; pos++) {
                    Item mChild = getChildAt(pos);
                    if (mChild == null)
                        continue;
                    T result = mChild.findItemById(id);
                    if (result == null)
                        continue;
                    return result;
                }
            }

            return null;
        }

        boolean isExpanded();

        boolean setExpanded(boolean expanded);

        default boolean toggleExpansion() {
            return setExpanded(!isExpanded());
        }

        default void setChildItemsExpanded(boolean expanded) {
            synchronized (this) {
                int childCount = getChildCount();
                for (int pos = 0; pos < childCount; pos++) {
                    Item childItem = getChildAt(pos);
                    if (childItem instanceof ItemGroup)
                        ((ItemGroup) childItem).setExpanded(expanded);
                }
            }
        }
    }

    public interface ItemManager {
        default void detachAll() {
            synchronized (this) {
                int childCount = getChildCount();
                for (int pos = childCount - 1; pos >= 0; pos--)
                    detachChild(pos);
            }
        }

        default void attachChild(@Nullable Item child) {
            synchronized (this) {
                int childCount = getChildCount();
                attachChild(childCount, child);
            }
        }

        void attachChild(@IntRange(from = 0) int index, @Nullable Item child);

        @SuppressWarnings("PointlessNullCheck")
        default boolean detachChild(@Nullable Item child) {
            boolean detached = false;
            synchronized (this) {
                int childCount = getChildCount();
                for (int pos = childCount - 1; pos >= 0; pos--) {
                    Item mChild = getChildAt(pos);
                    if ((mChild == null && child == null) ||
                            (mChild != null && child != null && mChild.equals(child))) {
                        detachChild(pos);
                        detached = true;
                    }
                }
            }
            return detached;
        }

        void detachChild(@IntRange(from = 0) int index);

        @IntRange(from = 0)
        int getChildCount();

        Item getChildAt(@IntRange(from = 0) int index);
    }

    public static final class Helper {
        private Helper() {
        }

        public static boolean setAllLevelsExpanded(@Nullable final Tree tree, boolean expanded) {
            return setAllLevelsExpanded(tree != null ? tree.getRoot() : null, expanded);
        }

        public static boolean setAllLevelsExpanded(@Nullable final Item item, boolean expanded) {
            if (item == null)
                return true;

            boolean applied = true;

            if (item instanceof ItemGroup) {
                final ItemGroup group = (ItemGroup) item;
                final boolean changeNeeded = group.isExpanded() != expanded;
                applied = !changeNeeded || group.setExpanded(expanded);
            }

            if (item instanceof ItemManager) {
                final ItemManager itemManager = (ItemManager) item;
                synchronized (itemManager) {
                    int childCount = itemManager.getChildCount();
                    for (int pos = 0; pos < childCount; pos++) {
                        Item childItem = itemManager.getChildAt(pos);
                        applied &= setAllLevelsExpanded(childItem, expanded);
                    }
                }
            }

            return applied;
        }
    }
}
