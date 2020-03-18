package com.simplelib.interfaces;

public interface Selectable {
    default boolean isSelectable() {
        return true;
    }

    boolean isSelected();

    boolean setSelected(boolean selected);

    default boolean toggleSelection() {
        return setSelected(!isSelected());
    }
}
