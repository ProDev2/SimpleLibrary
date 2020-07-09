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