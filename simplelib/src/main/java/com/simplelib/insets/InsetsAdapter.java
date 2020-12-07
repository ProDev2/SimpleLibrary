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

package com.simplelib.insets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public abstract class InsetsAdapter<I extends Insets> {
    protected final long id;

    private I appliedInsets;
    private I innerInsets;

    public InsetsAdapter(long id) {
        this.id = id;
    }

    @SuppressWarnings({"ConstantConditions",
            "SynchronizationOnLocalVariableOrMethodParameter",
            "unchecked",
            "unused"})
    public final void transfer(@NonNull InsetsAdapter<I> insetsAdapter) {
        if (insetsAdapter == null)
            throw new NullPointerException("No insets adapter attached");
        if (this.id != insetsAdapter.id)
            throw new IllegalArgumentException("Cannot transfer to unequal insets adapter");

        synchronized (this) {
            if (this.appliedInsets != null) {
                synchronized (insetsAdapter) {
                    insetsAdapter.appliedInsets = (I) this.appliedInsets.copy();

                    if (this.innerInsets != null) {
                        insetsAdapter.innerInsets = (I) this.innerInsets.copy();
                    }
                }
            }
        }
    }

    public final long getId() {
        return id;
    }

    @SuppressWarnings({"SynchronizeOnNonFinalField", "unused"})
    @Nullable
    public final I getAppliedInsets() {
        if (this.appliedInsets != null) {
            synchronized (this.appliedInsets) {
                this.appliedInsets.setStable(true);
            }
        }

        return this.appliedInsets;
    }

    @SuppressWarnings({"SynchronizeOnNonFinalField", "unused"})
    @Nullable
    public final I getInnerInsets() {
        if (this.innerInsets != null) {
            synchronized (this.innerInsets) {
                this.innerInsets.setStable(true);
            }
        }

        return this.innerInsets;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    protected void onApplyInsets(@NonNull I insets) {
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    @Nullable
    protected I onAssembleInnerInsets(@NonNull I insets) {
        return null;
    }

    @Nullable
    public final I apply(@NonNull I insets, boolean require) {
        return apply(insets, require, null);
    }

    @SuppressWarnings({"unchecked",
            "ConstantConditions",
            "SynchronizationOnLocalVariableOrMethodParameter",
            "SynchronizeOnNonFinalField", "unused"})
    @Nullable
    public final I apply(@NonNull I insets, boolean require, @Nullable AtomicBoolean changed) {
        if (insets == null)
            throw new NullPointerException("No insets attached");

        synchronized (this) {
            boolean updateApplied = require ||
                    this.appliedInsets == null ||
                    !this.appliedInsets.equalInsets(insets);
            boolean updateInner = require ||
                    updateApplied ||
                    this.innerInsets == null;

            if (changed != null) {
                synchronized (changed) {
                    changed.set(updateApplied ||
                            updateInner);
                }
            }

            if (updateApplied) {
                synchronized (insets) {
                    insets.setStable(true);
                    try {
                        onApplyInsets(insets);
                    } finally {
                        insets.setStable(true);
                    }
                    this.appliedInsets = insets;
                }
            }

            if (updateApplied || updateInner) {
                I innerInsets = (I) this.appliedInsets.copy();
                if (innerInsets != null) {
                    synchronized (innerInsets) {
                        innerInsets.setStable(false);
                        try {
                            this.innerInsets = onAssembleInnerInsets(innerInsets);

                            if (this.innerInsets != null) {
                                synchronized (this.innerInsets) {
                                    if (this.innerInsets != innerInsets)
                                        this.innerInsets.setStable(true);
                                }
                            }
                        } finally {
                            innerInsets.setStable(true);
                        }
                    }
                } else {
                    this.innerInsets = null;
                }
            }

            return this.innerInsets;
        }
    }
}
