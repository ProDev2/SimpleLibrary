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

package com.simplelib.parser;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"FinalStaticMethod",
        "unused",
        "PointlessArithmeticExpression",
        "WeakerAccess"})
public final class Size {
    // Shifting
    private static final int SHIFT_1 = 29;
    private static final int SHIFT_2 = SHIFT_1 - 1;

    // Masks
    private static final int MASK_1 = 0x7 << SHIFT_1;
    private static final int MASK_2 = 0x1 << SHIFT_2;

    private static final int MASK_ALL = MASK_1 | MASK_2;

    // Flags (Types)
    public static final int UNDEFINED = 0x0;
    public static final int UNIT = MASK_1;

    public static final int UNIT_DP = 0x3 << (SHIFT_1 + 1);
    public static final int UNIT_SP = 0x3 << (SHIFT_1 + 0);
    public static final int UNIT_IN = 0x1 << (SHIFT_1 + 2);
    public static final int UNIT_PT = 0x1 << (SHIFT_1 + 1);
    public static final int UNIT_MM = 0x1 << (SHIFT_1 + 0);

    // Bitwise operations (Initialization)
    public static final boolean isValid(int size) {
        if (size < 0)
            size = -size;

        return (size & ~MASK_ALL) == size;
    }

    public static final int with(int size) {
        return with(UNIT, size);
    }

    public static final int with(@Type int type, int size) {
        int flag = type;
        if (size >= 0) {
            flag &= ~MASK_2;
        } else {
            size = -size;
            flag |= MASK_2;
        }

        return (flag & MASK_ALL) | (size & ~MASK_ALL);
    }

    // Bitwise operations (Type)
    public static final boolean hasType(int value) {
        return (value & MASK_1) != (UNDEFINED & MASK_1);
    }

    public static final boolean isType(int value, @Type int type) {
        return (value & MASK_1) == (type & MASK_1);
    }

    @SuppressLint("WrongConstant")
    @Type
    public static final int getType(int value) {
        return (value & MASK_1);
    }

    public static final int setType(int value, @Type int type) {
        int filteredValue = (value & ~MASK_1);
        return filteredValue | (type & MASK_1);
    }

    // Bitwise operations (Size)
    public static final boolean isPositive(int value) {
        return (value & MASK_2) != MASK_2;
    }

    public static final boolean isNegative(int value) {
        return (value & MASK_2) == MASK_2;
    }

    public static final int setPositive(int value) {
        return (value & ~MASK_2);
    }

    public static final int setNegative(int value) {
        return (value | MASK_2);
    }

    @SuppressWarnings("ConstantConditions")
    public static final int get(int value) {
        boolean isNegative = (value & MASK_2) == MASK_2;
        int size = (value & ~MASK_ALL);
        return (isNegative && size >= 0) || (!isNegative && size < 0)
                ? -size
                : size;
    }

    public static final int set(int value, int size) {
        int filteredValue = (value & ~(MASK_2 | ~MASK_ALL));

        int flag = 0x0;
        if (size < 0) {
            size = -size;
            flag = MASK_2;
        }

        return filteredValue | flag | (size & ~MASK_ALL);
    }

    // Converter operations
    public static final float getConvertedSize(int value) {
        int type = getType(value);
        int size = get(value);
        return convert(type, size);
    }

    public static final float getConvertedSize(int value, int defSize) {
        int type = getType(value);
        int size = get(value);
        try {
            return convert(type, size);
        } catch (Throwable tr) {
            return defSize;
        }
    }

    // Converter
    public static final float convert(@Type int type, int size) {
        return convert(type, (float) size);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static final float convert(@Type int type, float size) {
        final int unit;
        switch (type & MASK_ALL) {
            default:
                throw new IllegalArgumentException("Invalid type");

            case UNDEFINED:
                return size;

            case UNIT:
                return size;

            case UNIT_DP:
                unit = TypedValue.COMPLEX_UNIT_DIP;
                break;

            case UNIT_SP:
                unit = TypedValue.COMPLEX_UNIT_SP;
                break;

            case UNIT_IN:
                unit = TypedValue.COMPLEX_UNIT_IN;
                break;

            case UNIT_PT:
                unit = TypedValue.COMPLEX_UNIT_PT;
                break;

            case UNIT_MM:
                unit = TypedValue.COMPLEX_UNIT_MM;
                break;
        }

        final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(
                unit,
                size,
                metrics
        );
    }

    // Private constructor
    private Size() {}

    @SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = false, value = {
            UNDEFINED,
            UNIT,
            UNIT_DP,
            UNIT_SP,
            UNIT_IN,
            UNIT_PT,
            UNIT_MM
    })
    public @interface Type {}
}
