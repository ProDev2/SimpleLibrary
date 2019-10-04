package com.simplelib.interfaces;

public interface OnEvent {
    default boolean onBack() {
        return true;
    }
}
