package com.prodev.simple.states;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prodev.simple.R;
import com.simplelib.adapter.SimpleRecyclerAdapter;
import com.simplelib.container.SimpleCheckItem;
import com.simplelib.container.SimpleItem;
import com.simplelib.struct.Tree;

public enum Fruits implements Tree.ItemCreator<String> {
    C1(1, "Category 1"),
        TEST(2, "Test"),
    C2(1, "Category 2"),
    C3(1, "Category 3", "Fruits"),
        APPLE(2, "Apple"),
        BANANA(2, "Banana"),
            GOOD(3, "Good", "Selectable"),
            BAD(3, "Bad", "Selectable"),
    C4(1, "Category 4"),
    C5(1, "Category 5");

    private int level;

    private String text;
    private String subtext;

    private Fruits(int level, String text, String subtext) {
        this.level = level;

        this.text = text;
        this.subtext = subtext;
    }

    private Fruits(int level, String text) {
        this.level = level;

        this.text = text;
    }

    @Override
    public int getLevel(@Nullable String value) {
        return level;
    }

    @NonNull
    @Override
    public Tree.Item create(@Nullable String value) {
        final SimpleCheckItem item = new SimpleCheckItem();
        item.setCheckable(level > 1);
        item.setText(text);
        item.setSubText(subtext);

        item.image.iconRes = R.mipmap.ic_launcher_round;

        return new FruitItem(item);
    }

    public static class FruitItem extends Tree.IItemGroup {
        private final SimpleItem data;

        public FruitItem(@NonNull SimpleItem data) {
            if (data == null)
                throw new NullPointerException("No data attached");

            this.data = data;
        }

        @NonNull
        public SimpleItem getData() {
            return data;
        }
    }

    public static class FruitProvider implements SimpleRecyclerAdapter.Provider<Tree.Item, SimpleItem> {
        @Nullable
        @Override
        public SimpleItem provide(Tree.Item item, int pos) {
            if (item instanceof FruitItem)
                return ((FruitItem) item).getData();
            return null;
        }

        @Override
        public SimpleRecyclerAdapter.Provider<Tree.Item, SimpleItem> get() {
            return new FruitProvider();
        }
    }
}
