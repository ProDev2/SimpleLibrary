package com.simplelib.insets;

import androidx.annotation.NonNull;

public interface Insets {
    void setStable(boolean stable);

    boolean isConsumed();
    void consume();

    @NonNull
    Insets copy();

    boolean equalInsets(@NonNull Insets insets);
}
