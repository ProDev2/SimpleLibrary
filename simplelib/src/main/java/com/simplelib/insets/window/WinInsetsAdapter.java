/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.insets.window;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.simplelib.insets.InsetsAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WinInsetsAdapter extends InsetsAdapter<WinInsets> {
    public static boolean USE_VIEW_TAG = false;

    protected static final int VIEW_TAG_KEY;

    static {
        int vtk = 1678252;
        VIEW_TAG_KEY = vtk | 2 << 24;
    }

    @NonNull
    private final View view;

    public WinInsetsAdapter(@NonNull View view) {
        super(id(view));

        if (view == null)
            throw new NullPointerException("No view attached");

        this.view = view;

        attach();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public WinInsetsAdapter(@NonNull View view,
                            boolean applyInsetsListener) {
        this(view, applyInsetsListener, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public WinInsetsAdapter(@NonNull View view,
                            boolean applyInsetsListener,
                            boolean consumeInsetsListener) {
        this(view);

        if (view == null)
            throw new NullPointerException("No view attached");

        if (applyInsetsListener) {
            view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                    if (view != WinInsetsAdapter.this.view)
                        return insets;
                    if (insets == null)
                        return null;

                    if (view != null) {
                        WinInsets winInsets = WinInsets.with(insets);
                        applyInsets(view, winInsets, false);
                    }

                    WindowInsets nextInsets = insets;
                    if (consumeInsetsListener) {
                        nextInsets = nextInsets.consumeSystemWindowInsets();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            nextInsets = nextInsets.consumeStableInsets();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            nextInsets = nextInsets.consumeDisplayCutout();
                        }
                    }
                    return nextInsets;
                }
            });
        }
    }

    public void attach() {
        // Get current insets
        WinInsets currentInsets = getAppliedWindowInsets(view);

        // Attach adapter to view
        boolean alreadyAttached = false;
        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag == null)
                view.setTag(this);
            else if (currentTag instanceof WinInsetsAdapter &&
                    currentTag != this)
                alreadyAttached = true;
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag == null)
                view.setTag(VIEW_TAG_KEY, this);
            else if (currentTag instanceof WinInsetsAdapter &&
                    currentTag != this)
                alreadyAttached = true;
        }

        if (alreadyAttached)
            throw new IllegalStateException("Insets adapter is already defined");

        if (currentTag == this)
            return;

        // Apply current view insets to adapter
        if (currentInsets != null)
            applyInsets(view, currentInsets, false);
    }

    public void detach() {
        // Get current insets
        WinInsets currentInsets = getAppliedWindowInsets(view);

        // Detach adapter from view
        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag == this)
                view.setTag(null);
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag == this)
                view.setTag(VIEW_TAG_KEY, null);
        }

        if (currentTag != this)
            return;

        // Apply current adapter insets to view
        if (currentInsets != null)
            applyInsets(view, currentInsets, false);
    }

    public static WinInsets getAppliedWindowInsets(@NonNull View view) {
        if (view == null)
            throw new NullPointerException("No view attached");

        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag instanceof WinInsets)
                return ((WinInsets) currentTag);
            else if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag).getAppliedInsets();
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag instanceof WinInsets)
                return ((WinInsets) currentTag);
            else if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag).getAppliedInsets();
        }

        return null;
    }

    public static WinInsets getInnerWindowInsets(@NonNull View view) {
        if (view == null)
            throw new NullPointerException("No view attached");

        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag instanceof WinInsets)
                return ((WinInsets) currentTag);
            else if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag).getInnerInsets();
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag instanceof WinInsets)
                return ((WinInsets) currentTag);
            else if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag).getInnerInsets();
        }

        return null;
    }

    @Nullable
    public static WinInsetsAdapter getAdapter(@NonNull View view) {
        if (view == null)
            throw new NullPointerException("No view attached");

        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag);
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag instanceof WinInsetsAdapter)
                return ((WinInsetsAdapter) currentTag);
        }

        return null;
    }

    public static long id(@NonNull View view) {
        if (view == null)
            throw new NullPointerException("No view attached");

        return view.hashCode();
    }

    public static void applyInsets(@NonNull View view, @NonNull WinInsets insets, boolean require) {
        if (view == null)
            throw new NullPointerException("No view attached");
        if (insets == null)
            throw new NullPointerException("No insets attached");

        // Handle insets internally
        AtomicBoolean changed = new AtomicBoolean(false);
        applyInternal(view, insets, require, changed);
    }

    private static void applyInternal(@NonNull View view,
                                      @NonNull WinInsets insets,
                                      boolean require,
                                      @Nullable AtomicBoolean changed) {
        // Reset changed value
        if (changed != null) {
            synchronized (changed) {
                changed.set(false);
            }
        }

        // Apply insets and get inner insets
        WinInsets innerInsets = applyInsetsAndGetInner(view, insets, require, changed);
        if (innerInsets == null)
            return;
        if (!require && changed != null && !changed.get())
            return;

        if (!(view instanceof ViewGroup))
            return;

        // Apply inner insets to child views
        ViewGroup group = (ViewGroup) view;

        int childCount = group.getChildCount();
        for (int pos = 0; pos < childCount; pos++) {
            View childView = group.getChildAt(pos);
            if (childView == null)
                continue;

            // Copy inner insets
            WinInsets childInsets = WinInsets.copy(innerInsets);
            if (childInsets == null)
                continue;

            // Apply inner insets
            applyInternal(childView, childInsets, require, changed);
        }
    }

    @Nullable
    private static WinInsets applyInsetsAndGetInner(@NonNull View view,
                                                    @NonNull WinInsets insets,
                                                    boolean require,
                                                    @Nullable AtomicBoolean changed) {
        if (view == null)
            return insets;
        if (insets == null)
            return null;

        WinInsets innerInsets;

        Object currentTag;

        if (USE_VIEW_TAG) {
            currentTag = view.getTag();
            if (currentTag instanceof WinInsetsAdapter)
                innerInsets = ((WinInsetsAdapter) currentTag).apply(insets, require, changed);
            else {
                if (changed != null &&
                        require ||
                        currentTag == null ||
                        !(currentTag instanceof WinInsets) ||
                        !((WinInsets) currentTag).equalInsets(insets)) {
                    synchronized (changed) {
                        changed.set(true);
                    }
                }

                view.setTag(insets);
                innerInsets = insets;
            }
        } else {
            currentTag = view.getTag(VIEW_TAG_KEY);
            if (currentTag instanceof WinInsetsAdapter)
                innerInsets = ((WinInsetsAdapter) currentTag).apply(insets, require, changed);
            else {
                if (changed != null &&
                        require ||
                        currentTag == null ||
                        !(currentTag instanceof WinInsets) ||
                        !((WinInsets) currentTag).equalInsets(insets)) {
                    synchronized (changed) {
                        changed.set(true);
                    }
                }

                view.setTag(VIEW_TAG_KEY, insets);
                innerInsets = insets;
            }
        }

        if (innerInsets != null && innerInsets.isConsumed())
            return null;

        return innerInsets;
    }
}
