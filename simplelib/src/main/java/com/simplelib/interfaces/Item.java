package com.simplelib.interfaces;

import androidx.annotation.NonNull;

import com.simplelib.holder.ImageHolder;
import com.simplelib.holder.TextHolder;

public interface Item {
    @NonNull
    ImageHolder getImage();

    @NonNull
    ImageHolder getSubImage();

    @NonNull
    TextHolder getText();

    @NonNull
    TextHolder getSubText();
}
