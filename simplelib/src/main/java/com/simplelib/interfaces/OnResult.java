package com.simplelib.interfaces;

public interface OnResult<V> {
    void onResult(V value);

    void onError();
}