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

package com.simplelib.interfaces;

import android.annotation.SuppressLint;
import android.text.Spannable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

@SuppressLint("Range")
public interface SpanHolder {
    int NO_POS = -1;

    boolean applyTo(@NonNull Spannable spannable, @IntRange(from = 0) int start, @IntRange(from = 0) int end);

    static boolean applyTo(@NonNull SpanHolder span, @NonNull Spannable spannable) {
        return applyTo(span, spannable, NO_POS, NO_POS);
    }

    static boolean applyTo(@NonNull SpanHolder span, @NonNull Spannable spannable, @IntRange(from = -1) int start, @IntRange(from = -1) int end) {
        try {
            start = getStart(start);
            end = getEnd(spannable, end);

            return span.applyTo(spannable, start, end);
        } catch (Throwable tr) {
            tr.printStackTrace();
            return false;
        }
    }

    @IntRange(from = 0)
    static int getStart(@IntRange(from = -1) int start) {
        return start >= 0 ? start : 0;
    }
    
    @IntRange(from = 0)
    static int getEnd(@NonNull Spannable spannable, @IntRange(from = -1) int end) {
        int len = spannable.length();
        return end >= 0 && end <= len ? end : len;
    }
}