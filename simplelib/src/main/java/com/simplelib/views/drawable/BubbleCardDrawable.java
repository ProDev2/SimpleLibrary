package com.simplelib.views.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.simplelib.math.Line;
import com.simplelib.math.Vector2;

public class BubbleCardDrawable extends Drawable {
    //Static variables
    private int color = Color.WHITE;

    //Variables
    private boolean roundCorners;
    private double cornerRadius;

    private double arrowSize;
    private double arrowCornerRadius;

    private Vector2 arrowTarget;

    //Draw
    private int alpha;
    private ColorFilter colorFilter;

    private Paint paint;

    //Calculation
    private Rect bounds;
    private Rect bubbleBounds;

    private Line lineLeft;
    private Line lineTop;
    private Line lineRight;
    private Line lineBottom;

    private Path path;

    public BubbleCardDrawable() {
        initialize();
    }

    public BubbleCardDrawable(int color) {
        this.color = color;
        initialize();
    }

    public BubbleCardDrawable(int color, double cornerRadius, double arrowSize, double arrowCornerRadius) {
        this.color = color;
        this.cornerRadius = cornerRadius;
        this.arrowSize = arrowSize;
        this.arrowCornerRadius = arrowCornerRadius;
        initialize();
    }

    public BubbleCardDrawable(int color, boolean roundCorners, double cornerRadius, double arrowSize, double arrowCornerRadius) {
        this.color = color;
        this.roundCorners = roundCorners;
        this.cornerRadius = cornerRadius;
        this.arrowSize = arrowSize;
        this.arrowCornerRadius = arrowCornerRadius;
        initialize();
    }

    private void initialize() {
        update();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    private void update() {
        //if (cornerRadius < 0) cornerRadius = 0;
        //if (arrowSize < 0) arrowSize = 0;
        //if (arrowCornerRadius < 0) arrowCornerRadius = 0;
    }

    public Paint getPaint() {
        return paint;
    }

    public int getColor() {
        return color;
    }

    public boolean isRoundCorners() {
        return roundCorners;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    public double getArrowSize() {
        return arrowSize;
    }

    public double getArrowCornerRadius() {
        return arrowCornerRadius;
    }

    public Vector2 getArrowTarget() {
        return arrowTarget;
    }

    public Rect getFullBounds() {
        return bounds;
    }

    public Rect getBubbleBounds() {
        return bubbleBounds;
    }

    public void setColor(int color) {
        boolean changed = this.color != color;
        this.color = color;
        this.paint.setColor(color);
        if (changed) {
            invalidateSelf();
        }
    }

    public void setRoundCorners(boolean roundCorners) {
        boolean changed = this.roundCorners != roundCorners;
        this.roundCorners = roundCorners;
        if (changed) {
            path = getPath(true);
            invalidateSelf();
        }
    }

    public void setCornerRadius(double cornerRadius) {
        boolean changed = this.cornerRadius != cornerRadius;
        this.cornerRadius = cornerRadius;
        if (changed) {
            path = getPath(true);
            invalidateSelf();
        }
    }

    public void setArrowSize(double arrowSize) {
        boolean changed = this.arrowSize != arrowSize;
        this.arrowSize = arrowSize;
        if (changed) {
            calculateBounds(bounds, true);
            invalidateSelf();
        }
    }

    public void setArrowCornerRadius(double arrowCornerRadius) {
        boolean changed = this.arrowCornerRadius != arrowCornerRadius;
        this.arrowCornerRadius = arrowCornerRadius;
        if (changed) {
            path = getPath(true);
            invalidateSelf();
        }
    }

    public void setArrowTarget(Vector2 arrowTarget) {
        this.arrowTarget = arrowTarget;

        path = getPath(true);
        invalidateSelf();
    }

    public void setFullBounds(Rect bounds) {
        this.bounds = bounds;

        calculateBounds(bounds, true);
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (bounds == null) {
            bounds = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            calculateBounds(bounds, true);
        }

        path = getPath(false);

        if (path != null && paint != null) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        calculateBounds(bounds, true);
    }

    @Override
    public void getOutline(@NonNull Outline outline) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                double cornerRadius = calculateCornerRadius();

                if (bubbleBounds != null && cornerRadius >= 0) {
                    if (roundCorners)
                        outline.setRoundRect(bubbleBounds, (float) cornerRadius);
                    else
                        outline.setRect(bubbleBounds);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        this.paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Nullable
    @Override
    public ColorFilter getColorFilter() {
        return colorFilter;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
        this.paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void calculateBounds(Rect bounds) {
        calculateBounds(bounds, false);
    }

    public void calculateBounds(Rect bounds, boolean rebuild) {
        if (bounds == null)
            bounds = this.bounds;
        if (bounds == null)
            bounds = getBounds();

        boolean changed = this.bounds != bounds;
        this.bounds = bounds;
        if (!changed && !rebuild) return;

        if (bounds == null) {
            this.bubbleBounds = null;
        } else {
            int offset = (int) calculateOffset();

            int left = bounds.left + offset;
            int top = bounds.top + offset;
            int right = bounds.right - offset;
            int bottom = bounds.bottom - offset;

            Rect bubbleBounds = new Rect(left, top, right, bottom);
            if (bubbleBounds != null && bubbleBounds.width() > 0 && bubbleBounds.height() > 0)
                this.bubbleBounds = bubbleBounds;
            else
                this.bubbleBounds = null;
        }

        path = getPath(rebuild);
    }

    public Path getPath(boolean rebuild) {
        //Check path
        if (path != null && !rebuild)
            return path;

        //Check for bounds
        if (bounds == null || bubbleBounds == null)
            return null;

        //Create or reset path
        if (path == null)
            path = new Path();
        path.reset();

        //Update & Calculate lines
        update();
        calculateLines();

        //Find line with arrow
        Line lineFromArrow = null;
        Line lineWithArrow = null;

        if (arrowTarget != null) {
            Line arrowToLineLeft = lineLeft != null ? lineLeft.closestLineFrom(arrowTarget) : null;
            Line arrowToLineTop = lineTop != null ? lineTop.closestLineFrom(arrowTarget) : null;
            Line arrowToLineRight = lineRight != null ? lineRight.closestLineFrom(arrowTarget) : null;
            Line arrowToLineBottom = lineBottom != null ? lineBottom.closestLineFrom(arrowTarget) : null;

            double arrowDistToLineLeft = arrowToLineLeft != null ? arrowToLineLeft.getLength() : -1;
            double arrowDistToLineTop = arrowToLineTop != null ? arrowToLineTop.getLength() : -1;
            double arrowDistToLineRight = arrowToLineRight != null ? arrowToLineRight.getLength() : -1;
            double arrowDistToLineBottom = arrowToLineBottom != null ? arrowToLineBottom.getLength() : -1;

            if (lineWithArrow == null || lineWithArrow.getLength() > arrowDistToLineLeft) {
                lineFromArrow = arrowToLineLeft;
                lineWithArrow = lineLeft;
            }
            if (lineWithArrow == null || lineWithArrow.getLength() > arrowDistToLineTop) {
                lineFromArrow = arrowToLineTop;
                lineWithArrow = lineTop;
            }
            if (lineWithArrow == null || lineWithArrow.getLength() > arrowDistToLineRight) {
                lineFromArrow = arrowToLineRight;
                lineWithArrow = lineRight;
            }
            if (lineWithArrow == null || lineWithArrow.getLength() > arrowDistToLineBottom) {
                lineFromArrow = arrowToLineBottom;
                lineWithArrow = lineBottom;
            }
        }

        //Calculate path
        Rect bounds = bubbleBounds;
        if (bounds == null) return path;

        int left = bounds.left;
        int top = bounds.top;
        int right = bounds.right;
        int bottom = bounds.bottom;

        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        int width = bounds.width();
        int height = bounds.height();

        double cornerRadius = calculateCornerRadius();
        if (cornerRadius < 0d) cornerRadius = 0d;

        double halfLengthX = (((double) width) / 2d) - cornerRadius;
        double halfLengthY = (((double) height) / 2d) - cornerRadius;

        double maxArrowLength = calculateMaxArrowLength();
        if (maxArrowLength < 0d) maxArrowLength = 0d;

        double halfArrowLength = maxArrowLength / 2d;

        //Basics
        boolean startPointSet = false;
        if (!startPointSet && lineLeft != null) {
            startPointSet = true;
            path.moveTo(lineLeft.getStartXAsFloat(), lineLeft.getStartYAsFloat());
        }
        if (!startPointSet && lineTop != null) {
            startPointSet = true;
            path.moveTo(lineTop.getStartXAsFloat(), lineTop.getStartYAsFloat());
        }
        if (!startPointSet && lineRight != null) {
            startPointSet = true;
            path.moveTo(lineRight.getStartXAsFloat(), lineRight.getStartYAsFloat());
        }
        if (!startPointSet && lineBottom != null) {
            startPointSet = true;
            path.moveTo(lineBottom.getStartXAsFloat(), lineBottom.getStartYAsFloat());
        }

        if (!startPointSet) {
            path.moveTo(left, centerX);
        }

        //Path left
        if (lineLeft != null && lineLeft.getLength() > 0) {
            path.lineTo(lineLeft.getEndXAsFloat(), lineLeft.getEndYAsFloat());
            if (lineWithArrow == null || lineWithArrow != lineLeft || arrowSize <= 0 || maxArrowLength <= 0) {
                path.lineTo(lineLeft.getStartXAsFloat(), lineLeft.getStartYAsFloat());
            } else if (lineWithArrow != null && lineFromArrow != null) {
                Vector2 arrowPos = lineFromArrow.getEnd();
                double relPos = lineWithArrow.closestRelativePos(arrowPos, false);

                if (arrowPos != null) {
                    Line movementLine = lineWithArrow.copy();

                    if (movementLine.getLength() >= maxArrowLength) {
                        movementLine.setLength(movementLine.getLength() - halfArrowLength);
                        movementLine.reverse();
                        movementLine.setLength(movementLine.getLength() - halfArrowLength);

                        Vector2 realArrowPos = movementLine.getPoint(relPos, false);
                        Line lineToTopArrow = new Line(realArrowPos, realArrowPos.copy().add(-arrowSize, 0));

                        //TODO: Add arrow
                    }
                }
            }
        }

        //Corner (left | top)
        if (lineLeft != null && lineTop != null) {
            path.lineTo(lineLeft.getStartXAsFloat(), lineLeft.getStartYAsFloat());

            if (roundCorners) {
                RectF cornerRect = new RectF(
                        lineLeft.getStartXAsFloat(),
                        lineTop.getStartYAsFloat(),
                        lineTop.getStartXAsFloat() + (float) cornerRadius,
                        lineLeft.getStartYAsFloat() + (float) cornerRadius
                );
                path.arcTo(cornerRect, -180, 90);
            } else {
                path.quadTo(lineTop.getStartXAsFloat() - (float) cornerRadius, lineLeft.getStartYAsFloat() - (float) cornerRadius, lineTop.getStartXAsFloat(), lineTop.getStartYAsFloat());
            }
        }

        //Path top
        if (lineTop != null && lineTop.getLength() > 0) {
            path.lineTo(lineTop.getStartXAsFloat(), lineTop.getStartYAsFloat());
            if (lineWithArrow == null || lineWithArrow != lineTop || arrowSize <= 0 || maxArrowLength <= 0) {
                path.lineTo(lineTop.getEndXAsFloat(), lineTop.getEndYAsFloat());
            }
        }

        //Corner (right | top)
        if (lineTop != null && lineRight != null) {
            path.lineTo(lineTop.getEndXAsFloat(), lineTop.getEndYAsFloat());

            if (roundCorners) {
                RectF cornerRect = new RectF(
                        lineTop.getEndXAsFloat() - (float) cornerRadius,
                        lineTop.getEndYAsFloat(),
                        lineRight.getStartXAsFloat(),
                        lineRight.getStartYAsFloat() + (float) cornerRadius
                );
                path.arcTo(cornerRect, -90, 90);
            } else {
                path.quadTo(lineTop.getEndXAsFloat() + (float) cornerRadius, lineRight.getStartYAsFloat() - (float) cornerRadius, lineRight.getStartXAsFloat(), lineRight.getStartYAsFloat());
            }
        }

        //Path right
        if (lineRight != null && lineRight.getLength() > 0) {
            path.lineTo(lineRight.getStartXAsFloat(), lineRight.getStartYAsFloat());
            if (lineWithArrow == null || lineWithArrow != lineRight || arrowSize <= 0 || maxArrowLength <= 0) {
                path.lineTo(lineRight.getEndXAsFloat(), lineRight.getEndYAsFloat());
            }
        }

        //Corner (right | bottom)
        if (lineRight != null && lineBottom != null) {
            path.lineTo(lineRight.getEndXAsFloat(), lineRight.getEndYAsFloat());

            if (roundCorners) {
                RectF cornerRect = new RectF(
                        lineBottom.getEndXAsFloat() - (float) cornerRadius,
                        lineRight.getEndYAsFloat() - (float) cornerRadius,
                        lineRight.getEndXAsFloat(),
                        lineBottom.getEndYAsFloat()
                );
                path.arcTo(cornerRect, 0, 90);
            } else {
                path.quadTo(lineBottom.getEndXAsFloat() + (float) cornerRadius, lineRight.getEndYAsFloat() + (float) cornerRadius, lineBottom.getEndXAsFloat(), lineBottom.getEndYAsFloat());
            }
        }

        //Path bottom
        if (lineBottom != null && lineBottom.getLength() > 0) {
            path.lineTo(lineBottom.getEndXAsFloat(), lineBottom.getEndYAsFloat());
            if (lineWithArrow == null || lineWithArrow != lineBottom || arrowSize <= 0 || maxArrowLength <= 0) {
                path.lineTo(lineBottom.getStartXAsFloat(), lineBottom.getStartYAsFloat());
            }
        }

        //Corner (left | bottom)
        if (lineBottom != null && lineLeft != null) {
            path.lineTo(lineBottom.getStartXAsFloat(), lineBottom.getStartYAsFloat());

            if (roundCorners) {
                RectF cornerRect = new RectF(
                        lineLeft.getEndXAsFloat(),
                        lineLeft.getEndYAsFloat() - (float) cornerRadius,
                        lineBottom.getStartXAsFloat() + (float) cornerRadius,
                        lineBottom.getStartYAsFloat()
                );
                path.arcTo(cornerRect, 90, 90);
            } else {
                path.quadTo(lineBottom.getStartXAsFloat() - (float) cornerRadius, lineLeft.getEndYAsFloat() + (float) cornerRadius, lineLeft.getEndXAsFloat(), lineLeft.getEndYAsFloat());
            }
        }

        //Close path
        path.close();

        //Return path
        return path;
    }

    private void calculateLines() {
        //Update
        update();

        //Reset bound lines
        lineLeft = null;
        lineTop = null;
        lineRight = null;
        lineBottom = null;

        //Calculate bound lines
        Rect bounds = bubbleBounds;
        if (bounds == null) return;

        int left = bounds.left;
        int top = bounds.top;
        int right = bounds.right;
        int bottom = bounds.bottom;

        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        int width = bounds.width();
        int height = bounds.height();

        double cornerRadius = calculateCornerRadius();
        if (cornerRadius < 0d) cornerRadius = 0d;

        double halfLengthX = (((double) width) / 2d) - cornerRadius;
        double halfLengthY = (((double) height) / 2d) - cornerRadius;

        lineLeft = new Line(left, centerY - halfLengthY, left, centerY + halfLengthY);
        lineTop = new Line(centerX - halfLengthX, top, centerX + halfLengthX, top);
        lineRight = new Line(right, centerY - halfLengthY, right, centerY + halfLengthY);
        lineBottom = new Line(centerX - halfLengthX, bottom, centerX + halfLengthX, bottom);
    }

    public double calculateCornerRadius() {
        Rect bounds = bubbleBounds;
        if (bounds == null)
            return cornerRadius >= 0 ? cornerRadius : 0;

        int halfWidth = (int) ((double) bounds.width() / 2f);
        int halfHeight = (int) ((double) bounds.height() / 2f);

        if (halfWidth >= halfHeight) {
            if (cornerRadius >= 0)
                return Math.min(cornerRadius, halfHeight);
            else
                return halfHeight;
        } else {
            if (cornerRadius >= 0)
                return Math.min(cornerRadius, halfWidth);
            else
                return halfWidth;
        }
    }

    public double calculateMaxArrowSize() {
        double radius = arrowCornerRadius >= 0 ? arrowCornerRadius : 0;
        double minLength = radius > 0 ? (double) ((Math.pow((Math.pow(radius, 2d) + Math.pow(radius, 2d)), 1d / 2d)) / 2d) : 0d;

        double size = arrowSize;

        return Math.max(minLength, size);
    }

    public double calculateMaxArrowLength() {
        double radius = arrowCornerRadius >= 0 ? arrowCornerRadius : 0;
        double minLength = radius > 0 ? (double) (Math.pow((Math.pow(radius, 2d) + Math.pow(radius, 2d)), 1d / 2d)) : 0d;

        double length = arrowSize * 2;

        return Math.max(minLength, length);
    }

    public double calculateOffset() {
        double maxArrowSize = calculateMaxArrowSize();
        if (maxArrowSize < 0) maxArrowSize = 0;

        return maxArrowSize;
    }
}