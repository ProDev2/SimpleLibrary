package com.simplelib.interfaces;

import androidx.annotation.Nullable;

import com.simplelib.holder.ImageHolder;
import com.simplelib.holder.TextHolder;

public interface Item {
    @Nullable
    default ImageHolder getImage() {
        return null;
    }

    @Nullable
    default ImageHolder getSubImage() {
        return null;
    }

    @Nullable
    default TextHolder getText() {
        return null;
    }

    @Nullable
    default TextHolder getSubText() {
        return null;
    }
}
