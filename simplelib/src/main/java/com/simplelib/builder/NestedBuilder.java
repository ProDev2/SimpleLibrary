package com.simplelib.builder;

public abstract class NestedBuilder<T, V> {
    private T parentBuilder;
    private OnResultListener<V> onResultListener;

    public <P extends NestedBuilder<T, V>> P withParentBuilder(T parentBuilder, OnResultListener<V> onResultListener) {
        this.parentBuilder = parentBuilder;
        this.onResultListener = onResultListener;

        return (P) this;
    }

    public T done() {
        if (onResultListener != null) {
            V value = build();
            onResultListener.onResult(value);
        }

        return parentBuilder;
    }

    public abstract V build();

    public interface OnResultListener<V> {
        void onResult(V result);
    }
}
