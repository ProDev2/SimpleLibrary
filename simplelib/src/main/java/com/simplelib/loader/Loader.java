package com.simplelib.loader;

public interface Loader {
    void preLoad();

    void load();

    void publish();

    void postLoad();
}
