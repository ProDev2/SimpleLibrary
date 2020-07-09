package com.simplelib.holder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.simplelib.interfaces.SpanHolder;

@SuppressWarnings({"unused", "UnusedReturnValue"})
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

    public @Nullable Object span;

    public @Nullable TextHolder prefix;
    public @Nullable TextHolder suffix;

    public TextHolder() {
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public TextHolder(TextHolder src) {
        if (src != null)
            src.applyTo(this);
    }

    public TextHolder(TextHolder prefix, TextHolder src, TextHolder suffix) {
        if (src != null)
            src.applyTo(this);
        if (prefix != null)
            this.prefix = TextHolder.of(prefix);
        if (suffix != null)
            this.suffix = TextHolder.of(suffix);
    }

    public TextHolder(@Nullable CharSequence text) {
        this.text = text;
    }

    public TextHolder(@Nullable String text) {
        this.text = text;
    }

    public TextHolder(@Nullable @StringRes Integer textRes) {
        this.textRes = textRes;
    }

    public void applyTo(TextHolder target) {
        if (target == null)
            return;

        target.text = text;
        target.textRes = textRes;

        target.span = span;

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

        this.span = null;

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
    public TextHolder setSpan(@Nullable Object span) {
        this.span = span;
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
    public String getTextAsString(Context context) {
        return getTextAsString(context, null);
    }

    @Nullable
    public String getTextAsString(Context context, @Nullable String defText) {
        String result = null;
        if (text != null) {
            try {
                result = text instanceof String ? (String) text : text.toString();
            } catch (Throwable ignored) {
            }
        }
        if (result == null && textRes != null && context != null) {
            try {
                result = context.getString(textRes);
            } catch (Throwable ignored) {
            }
        }
        if (result == null)
            result = defText;

        String pre = prefix != null && prefix != this ? prefix.getTextAsString(context, null) : null;
        String suf = suffix != null && suffix != this ? suffix.getTextAsString(context, null) : null;

        if (pre == null && suf == null)
            return result;

        return (pre != null ? pre : "") +
                (result != null ? result : "") +
                (suf != null ? suf : "");
    }

    @Nullable
    public CharSequence getText(Context context) {
        return getText(context, null);
    }

    @Nullable
    public CharSequence getText(Context context, @Nullable CharSequence defText) {
        CharSequence result = text;
        if (result == null && textRes != null && context != null) {
            try {
                result = context.getText(textRes);
            } catch (Throwable ignored) {
            }
        }

        format:
        if (result != null && span != null) {
            Spannable s;
            try {
                result = s = new SpannableString(result);
            } catch (Throwable tr) {
                break format;
            }

            span:
            try {
                int len = result.length();

                Object spanObj = span;
                if (spanObj instanceof SpanHolder) {
                    SpanHolder.applyTo(
                            (SpanHolder) spanObj,
                            s,
                            0,
                            len
                    );

                    break span;
                }

                try {
                    if (spanObj instanceof CharacterStyle)
                        spanObj = CharacterStyle.wrap((CharacterStyle) spanObj);
                } catch (Throwable ignored) {
                }
                s.setSpan(
                        spanObj,
                        0,
                        len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            } catch (Throwable ignored) {
            }
        }

        if (result == null)
            result = defText;

        CharSequence pre = prefix != null && prefix != this ? prefix.getText(context, null) : null;
        CharSequence suf = suffix != null && suffix != this ? suffix.getText(context, null) : null;

        if (pre == null && suf == null)
            return result;

        return TextUtils.concat(
                (pre != null ? pre : ""),
                (result != null ? result : ""),
                (suf != null ? suf : "")
        );
    }

    public void applyTo(TextView textView) {
        applyTo(textView, null);
    }

    public void applyTo(TextView textView, @Nullable CharSequence defText) {
        if (textView == null)
            return;

        CharSequence text = getText(textView.getContext(), defText);
        if (text != null) {
            textView.setText(text);
        } else if (textRes != null) {
            textView.setText(textRes);
        } else {
            textView.setText("");
        }
    }

    public boolean applyToOrHide(TextView textView) {
        return applyToOrHide(textView, null);
    }

    public boolean applyToOrHide(TextView textView, @Nullable CharSequence defText) {
        if (textView == null)
            return false;

        CharSequence text = getText(textView.getContext(), defText);
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
    public static String getTextAsString(TextHolder text, Context context) {
        return text != null ? text.getTextAsString(context) : null;
    }

    @Nullable
    public static String getTextAsString(TextHolder text, Context context, @Nullable String defText) {
        return text != null ? text.getTextAsString(context, defText) : defText;
    }

    @Nullable
    public static CharSequence getText(TextHolder text, Context context) {
        return text != null ? text.getText(context) : null;
    }

    @Nullable
    public static CharSequence getText(TextHolder text, Context context, @Nullable CharSequence defText) {
        return text != null ? text.getText(context, defText) : defText;
    }

    public static void applyTo(TextHolder text, TextView textView) {
        if (text != null && textView != null)
            text.applyTo(textView, null);
    }

    public static void applyTo(TextHolder text, TextView textView, @Nullable CharSequence defText) {
        if (text != null && textView != null)
            text.applyTo(textView, defText);
    }

    public static boolean applyToOrHide(TextHolder text, TextView textView) {
        if (text != null && textView != null) {
            return text.applyToOrHide(textView, null);
        } else if (textView != null) {
            textView.setVisibility(View.GONE);
            return false;
        }
        return false;
    }

    public static boolean applyToOrHide(TextHolder text, TextView textView, @Nullable CharSequence defText) {
        if (text != null && textView != null) {
            return text.applyToOrHide(textView, defText);
        } else if (textView != null) {
            textView.setVisibility(View.GONE);
            return false;
        }
        return false;
    }
}
