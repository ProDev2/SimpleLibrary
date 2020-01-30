package com.simplelib.drawable;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"unused",
        "WeakerAccess",
        "CopyConstructorMissesField"})
public class AdvancedDrawable extends WrapperDrawable {
    public static final int MODE_ABSOLUTE = 1;
    public static final int MODE_RELATIVE = 2;

    @CornerMode
    public transient int cM = MODE_ABSOLUTE;

    public transient float cTL = 0f;
    public transient float cTR = 0f;
    public transient float cBL = 0f;
    public transient float cBR = 0f;

    public transient boolean clipContent = true;

    public transient boolean shapeChanged = false;

    public transient int fillColor = 0x00000000;
    public transient int outerColor = 0x00000000;
    public transient int innerColor = 0x00000000;

    public transient int iL = 0;
    public transient int iT = 0;
    public transient int iR = 0;
    public transient int iB = 0;

    public transient int oX = 0;
    public transient int oY = 0;

    private Paint paint;
    private Path path;

    public AdvancedDrawable(@Nullable Drawable drawable) {
        super(drawable);
    }

    public AdvancedDrawable(@Nullable AdvancedDrawable src) {
        super(src != null ? src.getDrawable() : null);

        if (src != null)
            src.applyTo(this);
    }

    @SuppressWarnings("unused")
    public AdvancedDrawable(@Nullable Drawable drawable, @Nullable AdvancedDrawable src) {
        super(drawable);

        if (src != null)
            src.applyTo(this);
    }

    @NonNull
    public AdvancedDrawable copy() {
        return new AdvancedDrawable(this);
    }

    @SuppressWarnings("ConstantConditions")
    public void applyTo(@NonNull AdvancedDrawable target) {
        if (target == null)
            throw new NullPointerException("No target attached");

        applyShapeTo(target);
        applyColorsTo(target);
        applyInsetsTo(target);
        applyOffsetTo(target);
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public void applyShapeTo(@NonNull AdvancedDrawable target) {
        if (target == null)
            throw new NullPointerException("No target attached");

        target.cM = this.cM;

        target.cTL = this.cTL;
        target.cTR = this.cTR;
        target.cBL = this.cBL;
        target.cBR = this.cBR;

        target.clipContent = this.clipContent;

        target.shapeChanged = true;
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public void applyColorsTo(@NonNull AdvancedDrawable target) {
        if (target == null)
            throw new NullPointerException("No target attached");

        target.fillColor = this.fillColor;
        target.outerColor = this.outerColor;
        target.innerColor = this.innerColor;
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public void applyInsetsTo(@NonNull AdvancedDrawable target) {
        if (target == null)
            throw new NullPointerException("No target attached");

        target.iL = this.iL;
        target.iT = this.iT;
        target.iR = this.iR;
        target.iB = this.iB;
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess"})
    public void applyOffsetTo(@NonNull AdvancedDrawable target) {
        if (target == null)
            throw new NullPointerException("No target attached");

        target.oX = this.oX;
        target.oY = this.oY;
    }

    public void setCornerRadius(@CornerMode int cornerMode, float cRadius) {
        boolean changed = this.cM != cornerMode ||
                this.cTL != cRadius ||
                this.cTR != cRadius ||
                this.cBR != cRadius ||
                this.cBL != cRadius;

        this.cM = cornerMode;

        this.cTL = cRadius;
        this.cTR = cRadius;
        this.cBR = cRadius;
        this.cBL = cRadius;

        if (changed) {
            this.shapeChanged = true;

            invalidateSelf();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void setCornerRadius(@CornerMode int cornerMode,
                                float cTopLeft,
                                float cTopRight,
                                float cBottomRight,
                                float cBottomLeft) {
        boolean changed = this.cM != cornerMode ||
                this.cTL != cTopLeft ||
                this.cTR != cTopRight ||
                this.cBR != cBottomRight ||
                this.cBL != cBottomLeft;

        this.cM = cornerMode;

        this.cTL = cTopLeft;
        this.cTR = cTopRight;
        this.cBR = cBottomRight;
        this.cBL = cBottomLeft;

        if (changed) {
            this.shapeChanged = true;

            invalidateSelf();
        }
    }

    public void setSquare() {
        setCornerRadius(MODE_RELATIVE,
                0f,
                0f,
                0f,
                0f);
    }

    public void setRound() {
        setCornerRadius(MODE_RELATIVE,
                1f,
                1f,
                1f,
                1f);
    }

    public void setClipContent(boolean clipContent) {
        boolean changed = this.clipContent != clipContent;
        this.clipContent = clipContent;
        if (changed)
            invalidateSelf();
    }

    public void setShapeChanged() {
        boolean changed = !this.shapeChanged;
        this.shapeChanged = true;
        if (changed)
            invalidateSelf();
    }

    public void setFillColor(int fillColor) {
        boolean changed = this.fillColor != fillColor;
        this.fillColor = fillColor;

        if (changed)
            invalidateSelf();
    }

    public void setOuterColor(int outerColor) {
        boolean changed = this.outerColor != outerColor;
        this.outerColor = outerColor;

        if (changed)
            invalidateSelf();
    }

    public void setInnerColor(int innerColor) {
        boolean changed = this.innerColor != innerColor;
        this.innerColor = innerColor;

        if (changed)
            invalidateSelf();
    }

    public void setInsets(int insets) {
        boolean changed = this.iL != insets ||
                this.iT != insets ||
                this.iR != insets ||
                this.iB != insets;

        this.iL = insets;
        this.iT = insets;
        this.iR = insets;
        this.iB = insets;

        if (changed)
            invalidateSelf();
    }

    public void setInsets(int insetsLeft,
                          int insetsTop,
                          int insetsRight,
                          int insetsBottom) {
        boolean changed = this.iL != insetsLeft ||
                this.iT != insetsTop ||
                this.iR != insetsRight ||
                this.iB != insetsBottom;

        this.iL = insetsLeft;
        this.iT = insetsTop;
        this.iR = insetsRight;
        this.iB = insetsBottom;

        if (changed)
            invalidateSelf();
    }

    public void setOffset(int offsetX,
                          int offsetY) {
        boolean changed = this.oX != offsetX ||
                this.oY != offsetY;

        this.oX = offsetX;
        this.oY = offsetY;

        if (changed)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("CanvasSize")
    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)
            return;

        int left;
        int top;
        int right;
        int bottom;

        Rect bounds = getBounds();
        if (bounds != null) {
            left = bounds.left;
            top = bounds.top;
            right = bounds.right;
            bottom = bounds.bottom;
        } else {
            left = 0;
            top = 0;
            right = canvas.getWidth();
            bottom = canvas.getHeight();
        }

        int width = right - left;
        int height = bottom - top;

        if (width <= 0 || height <= 0)
            return;

        if (fillColor != 0x00000000) {
            canvas.drawColor(fillColor);
        }

        if (outerColor != 0x00000000) {
            if (paint == null)
                paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(outerColor);

            canvas.drawRect(left,
                    top,
                    right,
                    bottom,
                    paint);
        }

        boolean hasContent = getDrawable() != null;
        boolean hasInnerColor = innerColor != 0x00000000;

        boolean clipContent = this.clipContent;
        boolean clip = (clipContent && hasContent) ||
                hasInnerColor;

        if (clip) {
            float acTL = 0f;
            float acTR = 0f;
            float acBL = 0f;
            float acBR = 0f;

            if (cM == MODE_ABSOLUTE) {
                acTL = cTL;
                acTR = cTR;
                acBL = cBL;
                acBR = cBR;
            } else if (cM == MODE_RELATIVE) {
                float minSize = Math.min(width, height);

                acTL = (minSize / 2f) * cTL;
                acTR = (minSize / 2f) * cTR;
                acBL = (minSize / 2f) * cBL;
                acBR = (minSize / 2f) * cBR;
            }

            if (path == null || shapeChanged) {
                calculatePath(left,
                        top,
                        right,
                        bottom,
                        acTL,
                        acTR,
                        acBL,
                        acBR);

                shapeChanged = false;
            }

            clip = path != null;
        }

        int state = 0;
        if (clip) {
            state = canvas.save();

            canvas.clipPath(path);

            if (hasInnerColor) {
                canvas.drawColor(innerColor);
            }

            if (!clipContent)
                canvas.restoreToCount(state);
        }

        if (hasContent) {
            applyInnerBounds(left,
                    top,
                    right,
                    bottom);

            super.draw(canvas);
        }

        if (clip && clipContent)
            canvas.restoreToCount(state);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (bounds == null)
            return;

        shapeChanged = true;

        int left = bounds.left;
        int top = bounds.top;
        int right = bounds.right;
        int bottom = bounds.bottom;

        applyInnerBounds(left,
                top,
                right,
                bottom);
    }

    private synchronized void applyInnerBounds(int left,
                                               int top,
                                               int right,
                                               int bottom) {
        int iLeft = left + iL + oX;
        int iTop = top + iT + oY;
        int iRight = right - iR + oX;
        int iBottom = bottom - iB + oY;

        Drawable innerDrawable = getDrawable();
        if (innerDrawable != null) {
            innerDrawable.setBounds(iLeft,
                    iTop,
                    iRight,
                    iBottom);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private synchronized void calculatePath(float left,
                                            float top,
                                            float right,
                                            float bottom,
                                            float acTL,
                                            float acTR,
                                            float acBL,
                                            float acBR) {
        if (path == null)
            path = new Path();
        path.reset();

        if (acTL <= 0f &&
                acTR <= 0f &&
                acBL <= 0f &&
                acBR <= 0f) {
            path.addRect(left,
                    top,
                    right,
                    bottom,
                    Path.Direction.CW);
        } else {
            float lTopStart = left + acTL;
            float lTopEnd = right - acTR;
            float lRightStart = top + acTR;
            float lRightEnd = bottom - acBR;
            float lBottomStart = right - acBR;
            float lBottomEnd = left + acBL;
            float lLeftStart = bottom - acBL;
            float lLeftEnd = top + acTL;

            path.moveTo(left, lLeftEnd);

            RectF arcRect = new RectF(0f, 0f, 0f, 0f);

            // Top left corner
            path.lineTo(left, lLeftEnd);
            if (acTL <= 0f) {
                path.lineTo(left, top);
            } else {
                arcRect.set(left,
                        top,
                        lTopStart + (lTopStart - left),
                        lLeftEnd + (lLeftEnd - top));
                path.arcTo(arcRect, 180f, 90f);
            }

            // Top right corner
            path.lineTo(lTopEnd, top);
            if (acTL <= 0f) {
                path.lineTo(right, top);
            } else {
                arcRect.set(lTopEnd - (right - lTopEnd),
                        top,
                        right,
                        lRightStart + (lRightStart - top));
                path.arcTo(arcRect, 270f, 90f);
            }

            // Botton right corner
            path.lineTo(right, lRightEnd);
            if (acTL <= 0f) {
                path.lineTo(right, bottom);
            } else {
                arcRect.set(lBottomStart - (right - lBottomStart),
                        lRightEnd - (bottom - lRightEnd),
                        right,
                        bottom);
                path.arcTo(arcRect, 0f, 90f);
            }

            // Botton left corner
            path.lineTo(lBottomEnd, bottom);
            if (acTL <= 0f) {
                path.lineTo(left, bottom);
            } else {
                arcRect.set(left,
                        lLeftStart - (bottom - lLeftStart),
                        lBottomEnd + (lBottomEnd - left),
                        bottom);
                path.arcTo(arcRect, 90f, 90f);
            }
        }

        path.close();
    }

    @SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = false, value = {
            MODE_ABSOLUTE,
            MODE_RELATIVE
    })
    public @interface CornerMode {}
}