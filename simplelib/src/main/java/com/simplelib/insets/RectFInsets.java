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

import android.graphics.RectF;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class RectFInsets implements Insets {
    public static final int FLAG_INSETS_CONSUMED_LEFT = 0x1 << 0;
    public static final int FLAG_INSETS_CONSUMED_TOP = 0x1 << 1;
    public static final int FLAG_INSETS_CONSUMED_RIGHT = 0x1 << 2;
    public static final int FLAG_INSETS_CONSUMED_BOTTOM = 0x1 << 3;

    public static final int FLAG_INSETS_CONSUMED = 0xF;

    private final int insetsCount;

    private final int[] flags;
    private final RectF[] insets;

    private boolean stable;

    public RectFInsets(@IntRange(from = 0) int insetsCount) {
        this(new int[insetsCount], new RectF[insetsCount]);
    }

    public RectFInsets(@NonNull RectFInsets srcInsets) {
        this(srcInsets.flags, srcInsets.insets);
    }

    public RectFInsets(@Nullable int[] flags, @Nullable RectF[] insets) {
        if (flags == null)
            flags = new int[0];
        if (insets == null)
            insets = new RectF[0];

        int flagCount = flags.length;
        int insetsCount = insets.length;

        if (flagCount != insetsCount)
            throw new IllegalArgumentException("Flag amount is not equals insets amount");

        this.insetsCount = Math.min(flagCount, insetsCount);

        this.flags = new int[flagCount];
        this.insets = new RectF[insetsCount];

        for (int pos = 0; pos < this.insetsCount; pos++) {
            this.flags[pos] = flags[pos];
            this.insets[pos] = new RectF(insets[pos]);
        }
    }

    protected final void throwIfStable() {
        if (this.stable)
            throw new IllegalStateException("Insets are stable");
    }

    protected final void throwIfOutOfRange(int insetsIndex) {
        if (insetsIndex < 0 || insetsIndex >= this.insetsCount)
            throw new IndexOutOfBoundsException("Index at " + insetsIndex + " is out of range");
    }

    public boolean isStable() {
        return stable;
    }

    @Override
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    @Override
    public boolean isConsumed() {
        for (int pos = 0; pos < this.insetsCount; pos++) {
            if ((this.flags[pos] & FLAG_INSETS_CONSUMED) != FLAG_INSETS_CONSUMED)
                return false;
        }
        return true;
    }

    @Override
    public void consume() {
        throwIfStable();
        for (int pos = 0; pos < this.insetsCount; pos++) {
            this.flags[pos] |= FLAG_INSETS_CONSUMED;
            this.insets[pos].set(0f, 0f, 0f, 0f);
        }
    }

    protected int getInsetsCount() {
        return this.insetsCount;
    }

    protected void setFlags(int insetsIndex, int flags) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] = flags;
    }

    protected int getFlags(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return this.flags[insetsIndex];
    }

    protected boolean hasFlag(int insetsIndex, int flag) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & flag) != 0;
    }

    protected void addFlag(int insetsIndex, int flag) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= flag;
    }

    protected void removeFlag(int insetsIndex, int flag) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] &= ~flag;
    }

    protected boolean isConsumed(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & FLAG_INSETS_CONSUMED) == FLAG_INSETS_CONSUMED;
    }

    protected boolean isLeftConsumed(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & FLAG_INSETS_CONSUMED_LEFT) != 0;
    }

    protected boolean isTopConsumed(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & FLAG_INSETS_CONSUMED_TOP) != 0;
    }

    protected boolean isRightConsumed(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & FLAG_INSETS_CONSUMED_RIGHT) != 0;
    }

    protected boolean isBottomConsumed(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return (this.flags[insetsIndex] & FLAG_INSETS_CONSUMED_BOTTOM) != 0;
    }

    protected void consume(int insetsIndex) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= FLAG_INSETS_CONSUMED;
        this.insets[insetsIndex].set(0f, 0f, 0f, 0f);
    }

    protected void consumeLeft(int insetsIndex) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= FLAG_INSETS_CONSUMED_LEFT;
        this.insets[insetsIndex].left = 0f;
    }

    protected void consumeTop(int insetsIndex) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= FLAG_INSETS_CONSUMED_TOP;
        this.insets[insetsIndex].top = 0f;
    }

    protected void consumeRight(int insetsIndex) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= FLAG_INSETS_CONSUMED_RIGHT;
        this.insets[insetsIndex].right = 0f;
    }

    protected void consumeBottom(int insetsIndex) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.flags[insetsIndex] |= FLAG_INSETS_CONSUMED_BOTTOM;
        this.insets[insetsIndex].bottom = 0f;
    }

    protected void setInsets(int insetsIndex, @NonNull RectF insets) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        if (insets == null)
            throw new NullPointerException("No insets attached");
        this.insets[insetsIndex] = insets;
    }

    @NonNull
    protected RectF getInsets(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return !this.stable ? this.insets[insetsIndex] : new RectF(this.insets[insetsIndex]);
    }

    protected float getInsetsLeft(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return this.insets[insetsIndex].left;
    }

    protected float getInsetsTop(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return this.insets[insetsIndex].top;
    }

    protected float getInsetsRight(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return this.insets[insetsIndex].right;
    }

    protected float getInsetsBottom(int insetsIndex) {
        throwIfOutOfRange(insetsIndex);
        return this.insets[insetsIndex].bottom;
    }

    protected void setInsetsLeft(int insetsIndex, float left) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.insets[insetsIndex].left = left;
    }

    protected void setInsetsTop(int insetsIndex, float top) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.insets[insetsIndex].top = top;
    }

    protected void setInsetsRight(int insetsIndex, float right) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.insets[insetsIndex].right = right;
    }

    protected void setInsetsBottom(int insetsIndex, float bottom) {
        throwIfStable();
        throwIfOutOfRange(insetsIndex);
        this.insets[insetsIndex].bottom = bottom;
    }

    @Override
    public boolean equalInsets(@NonNull Insets insets) {
        if (insets == null || !(insets instanceof RectInsets))
            return false;

        RectFInsets rectInsets = (RectFInsets) insets;

        if (this.insetsCount != rectInsets.insetsCount)
            return false;

        int count = Math.min(this.insetsCount, rectInsets.insetsCount);
        for (int pos = 0; pos < count; pos++) {
            if (this.flags[pos] != rectInsets.flags[pos])
                return false;
            if (!this.insets[pos].equals(rectInsets.insets[pos]))
                return false;
        }

        return true;
    }

    public static final boolean checkForFlag(int flagsCombined, int flag) {
        return (flagsCombined & flag) != 0;
    }
}
