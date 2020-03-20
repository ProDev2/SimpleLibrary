package com.simplelib.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public final class TextHolder {
    // Initialization
    @NonNull
    public static TextHolder create() {
        return new TextHolder();
    }

    @NonNull
    public static TextHolder of(TextHolder src) {
        return new TextHolder(src);
    }

    @NonNull
    public static TextHolder of(TextHolder prefix, TextHolder src, TextHolder suffix) {
        return new TextHolder(prefix, src, suffix);
    }

    @NonNull
    public static TextHolder withText(CharSequence text) {
        return new TextHolder(text);
    }

    @NonNull
    public static TextHolder withText(String text) {
        return new TextHolder(text);
    }

    @NonNull
    public static TextHolder withTextRes(@StringRes int textRes) {
        return new TextHolder(textRes);
    }

    // Holder
    public @Nullable CharSequence text;
    public @Nullable @StringRes Integer textRes;

    public @Nullable TextHolder prefix;
    public @Nullable TextHolder suffix;

    public TextHolder() {
    }

    public TextHolder(TextHolder src) {
        if (src != null)
            src.applyTo(this);
    }

    public TextHolder(TextHolder prefix, TextHolder src, TextHolder suffix) {
        if (prefix != null)
            this.prefix = TextHolder.of(prefix);
        if (src != null)
            src.applyTo(this);
        if (suffix != null)
            this.suffix = TextHolder.of(suffix);
    }

    public TextHolder(CharSequence text) {
        this.text = text;
    }

    public TextHolder(String text) {
        this.text = text;
    }

    public TextHolder(@StringRes int textRes) {
        this.textRes = textRes;
    }

    public void applyTo(TextHolder target) {
        if (target == null)
            return;

        target.text = text;
        target.textRes = textRes;

        target.prefix = prefix != null ? TextHolder.of(prefix) : null;
        target.suffix = suffix != null ? TextHolder.of(suffix) : null;
    }

    public boolean hasText() {
        return this.text != null ||
                this.textRes != null ||
                (this.prefix != null && this.prefix != this && this.prefix.hasText()) ||
                (this.suffix != null && this.suffix != this && this.suffix.hasText());
    }

    @NonNull
    public TextHolder clear() {
        this.text = null;
        this.textRes = null;

        if (this.prefix != null) this.prefix.clear();
        if (this.suffix != null) this.suffix.clear();
        this.prefix = null;
        this.suffix = null;
        return this;
    }

    @NonNull
    public TextHolder setText(@Nullable CharSequence text) {
        this.text = text;
        return this;
    }

    @NonNull
    public TextHolder setTextRes(@Nullable Integer textRes) {
        this.textRes = textRes;
        return this;
    }

    @NonNull
    public TextHolder getPrefix() {
        if (prefix == null)
            prefix = TextHolder.create();
        return prefix;
    }

    @NonNull
    public TextHolder setPrefix(@Nullable TextHolder prefix) {
        if (prefix == this)
            prefix = TextHolder.of(prefix);
        this.prefix = prefix;
        return this;
    }

    @NonNull
    public TextHolder getSuffix() {
        if (suffix == null)
            suffix = TextHolder.create();
        return suffix;
    }

    @NonNull
    public TextHolder setSuffix(@Nullable TextHolder suffix) {
        if (suffix == this)
            suffix = TextHolder.of(suffix);
        this.suffix = suffix;
        return this;
    }

    @Nullable
    public String getText(Context context) {
        return getText(context, null);
    }

    @Nullable
    public String getText(Context context, String defText) {
        String result = null;
        if (text != null) {
            try {
                result = text instanceof String ? (String) text : text.toString();
            } catch (Exception e) {
            }
        } else if (textRes != null && context != null) {
            try {
                result = context.getString(textRes);
            } catch (Exception e) {
            }
        }
        if (result == null)
            result = defText;

        String pre = prefix != null && prefix != this ? prefix.getText(context) : null;
        String suf = suffix != null && suffix != this ? suffix.getText(context) : null;
        if (result != null || pre != null || suf != null) {
            return (pre != null ? pre : "") +
                    (result != null ? result : "") +
                    (suf != null ? suf : "");
        } else {
            return null;
        }
    }

    public void applyTo(TextView textView) {
        if (textView == null)
            return;

        String text = getText(textView.getContext());
        if (text != null) {
            textView.setText(text);
        } else if (textRes != null) {
            textView.setText(textRes);
        } else {
            textView.setText("");
        }
    }

    public boolean applyToOrHide(TextView textView) {
        if (textView == null)
            return false;

        String text = getText(textView.getContext());
        if (text != null) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            return true;
        } else if (textRes != null) {
            textView.setText(textRes);
            textView.setVisibility(View.VISIBLE);
            return true;
        } else {
            textView.setVisibility(View.GONE);
            return false;
        }
    }

    @Override
    public String toString() {
        if (text != null) {
            return text.toString();
        } else if (textRes != null) {
            return "StringRes:" + textRes;
        } else {
            return "";
        }
    }

    @Nullable
    public static String getText(TextHolder text, Context context) {
        return text != null ? text.getText(context) : null;
    }

    @Nullable
    public static String getText(TextHolder text, Context context, String defText) {
        return text != null ? text.getText(context, defText) : defText;
    }

    public static void applyTo(TextHolder text, TextView textView) {
        if (text != null && textView != null)
            text.applyTo(textView);
    }

    public static boolean applyToOrHide(TextHolder text, TextView textView) {
        if (text != null && textView != null) {
            return text.applyToOrHide(textView);
        } else if (textView != null) {
            textView.setVisibility(View.GONE);
            return false;
        }
        return false;
    }
}
