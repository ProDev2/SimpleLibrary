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
    public CharSequence text;
    public @Nullable @StringRes Integer textRes;

    public TextHolder() {
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

    public boolean hasText() {
        return this.text != null ||
                this.textRes != null;
    }

    @NonNull
    public TextHolder clear() {
        this.text = null;
        this.textRes = null;
        return this;
    }

    @NonNull
    public TextHolder setText(CharSequence text) {
        this.text = text;
        return this;
    }

    @NonNull
    public TextHolder setTextRes(int textRes) {
        this.textRes = textRes;
        return this;
    }

    @Nullable
    public String getText(Context ctx) {
        if (text != null) {
            return text instanceof String ? (String) text : text.toString();
        } else if (textRes != null && ctx != null) {
            try {
                return ctx.getString(textRes);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void applyTo(TextView textView) {
        if (textView == null)
            return;

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

    public static void applyTo(TextHolder text, TextView textView) {
        if (text != null && textView != null) {
            text.applyTo(textView);
        }
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
