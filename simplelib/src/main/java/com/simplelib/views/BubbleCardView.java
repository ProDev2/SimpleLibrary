package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.AttrRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import com.simplelib.R;
import com.simplelib.math.Line;
import com.simplelib.math.Vector2;
import com.simplelib.tools.Tools;
import com.simplelib.views.drawable.BubbleCardDrawable;

public class BubbleCardView extends ViewGroup {
    //Static variables
    private static final boolean DEFAULT_ROUND_CORNERS = true;
    private static final float DEFAULT_CORNER_RADIUS_DP = -1f;

    private static final boolean DEFAULT_NO_ARROW = false;
    private static final int DEFAULT_ARROW_TARGET_ID = 0;
    private static final float DEFAULT_ARROW_SIZE_DP = 5f;
    private static final float DEFAULT_ARROW_LENGTH_DP = 10f;
    private static final float DEFAULT_ARROW_CORNER_RADIUS_DP = 5f;

    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    //Variables (attributes)
    private boolean roundCorners;
    private float cornerRadius;

    private boolean noArrow;
    private int arrowTarget;
    private float arrowSize;
    private float arrowLength;
    private float arrowCornerRadius;

    private int backgroundColor;

    //Variables
    private Pointer pointer;

    //Draw
    private BubbleCardDrawable drawable;

    public BubbleCardView(Context context) {
        super(context);
        initialize(null, 0, 0);
    }

    public BubbleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0, 0);
    }

    public BubbleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        //Setup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                //TODO: Decide on whether or not to clip content to outline
                //setClipToOutline(true);
                setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            } catch (Exception e) {
            }
        }

        //Use defaults
        roundCorners = DEFAULT_ROUND_CORNERS;
        cornerRadius = Tools.dpToPx(DEFAULT_CORNER_RADIUS_DP);

        noArrow = DEFAULT_NO_ARROW;
        arrowTarget = DEFAULT_ARROW_TARGET_ID;
        arrowSize = Tools.dpToPx(DEFAULT_ARROW_SIZE_DP);
        arrowLength = Tools.dpToPx(DEFAULT_ARROW_LENGTH_DP);
        arrowCornerRadius = Tools.dpToPx(DEFAULT_ARROW_CORNER_RADIUS_DP);

        backgroundColor = DEFAULT_BACKGROUND_COLOR;

        //Fetch styled attributes
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleCardView, defStyleAttr, defStyleRes);

            roundCorners = attributes.getBoolean(R.styleable.BubbleCardView_bcv_roundCorners, roundCorners);
            cornerRadius = attributes.getDimension(R.styleable.BubbleCardView_bcv_cornerRadius, cornerRadius);

            noArrow = attributes.getBoolean(R.styleable.BubbleCardView_bcv_noArrow, noArrow);
            arrowTarget = attributes.getResourceId(R.styleable.BubbleCardView_bcv_arrowTarget, arrowTarget);
            arrowSize = attributes.getDimension(R.styleable.BubbleCardView_bcv_arrowSize, arrowSize);
            arrowLength = attributes.getDimension(R.styleable.BubbleCardView_bcv_arrowLength, arrowLength);
            arrowCornerRadius = attributes.getDimension(R.styleable.BubbleCardView_bcv_arrowCornerRadius, arrowCornerRadius);

            backgroundColor = attributes.getColor(R.styleable.BubbleCardView_bcv_backgroundColor, backgroundColor);

            attributes.recycle();
        }

        //Set background
        drawable = new BubbleCardDrawable(backgroundColor, roundCorners, cornerRadius, arrowSize, arrowLength, arrowCornerRadius);
        setBackground(drawable);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
        */

        //Add events
        /*
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean changed = false;
                try {
                    if (pointer != null)
                        changed = pointer.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (!changed && drawable != null)
                        drawable.redraw(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        */

        //Update
        update();
    }

    public BubbleCardDrawable getDrawable() {
        return drawable;
    }

    public void setColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        update();
    }

    public void setRoundCorners(boolean roundCorners) {
        this.roundCorners = roundCorners;
        update();
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        update();
    }

    public void setNoArrow(boolean noArrow) {
        this.noArrow = noArrow;
        update();
        requestLayout();
    }

    public void setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
        update();
    }

    public void setArrowLength(float arrowLength) {
        this.arrowLength = arrowLength;
        update();
    }

    public void setArrowCornerRadius(float arrowCornerRadius) {
        this.arrowCornerRadius = arrowCornerRadius;
        update();
    }

    public Pointer getPointer() {
        return pointer;
    }

    public void setPointer(Pointer pointer) {
        if (this.pointer != null) {
            this.pointer.setContentView(null, false);
            this.pointer.detach();
        }
        this.pointer = pointer;
        if (this.pointer != null) {
            this.pointer.setContentView(this, this.pointer.isAttached());
        }
        if (drawable != null)
            drawable.setArrowTarget(this.pointer);
    }

    public void update() {
        if (drawable != null) {
            drawable.setColor(backgroundColor);

            drawable.setRoundCorners(roundCorners);
            drawable.setCornerRadius(cornerRadius);

            drawable.setNoArrow(noArrow);
            drawable.setArrowSize(arrowSize);
            drawable.setArrowLength(arrowLength);
            drawable.setArrowCornerRadius(arrowCornerRadius);
        }
    }

    public void redraw(boolean rebuild) {
        if (drawable != null)
            drawable.redraw(rebuild);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Calculate constraints
        int offset = 0;
        if (drawable != null) offset = (int) drawable.calculateOffset();
        if (offset < 0) offset = 0;

        int widthConstraints = getPaddingLeft() + getPaddingRight() + (offset * 2);
        int heightConstraints = getPaddingTop() + getPaddingBottom() + (offset * 2);

        //Find rightmost and bottom-most child
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        for (int pos = 0; pos < count; pos++) {
            View child = getChildAt(pos);
            if (child == null) continue;

            int width = MeasureSpec.UNSPECIFIED;
            int height = MeasureSpec.UNSPECIFIED;

            int marginWidth = 0;
            int marginHeight = 0;

            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params != null) {
                width = params.width;
                height = params.height;
            }

            if (params != null && params instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) params;
                marginWidth = lp.leftMargin + lp.rightMargin;
                marginHeight = lp.topMargin + lp.bottomMargin;
            } else if (params != null && params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                marginWidth = lp.leftMargin + lp.rightMargin;
                marginHeight = lp.topMargin + lp.bottomMargin;
            }

            if (marginWidth < 0) marginWidth = 0;
            if (marginHeight < 0) marginHeight = 0;

            int childWidthMeasureSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    widthConstraints + marginWidth,
                    width
            );

            int childHeightMeasureSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    heightConstraints + marginHeight,
                    height
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if (child.getVisibility() != GONE) {
                int childRight = child.getMeasuredWidth() + marginWidth;
                int childBottom = child.getMeasuredHeight() + marginHeight;

                maxWidth = Math.max(maxWidth, childRight);
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }

        //Account for padding too
        maxWidth += widthConstraints;
        maxHeight += heightConstraints;

        //Check against our foreground's minimum height and width
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Drawable foregroundDrawable = getForeground();
            if (foregroundDrawable != null) {
                maxWidth = Math.max(maxWidth, foregroundDrawable.getMinimumWidth());
                maxHeight = Math.max(maxHeight, foregroundDrawable.getMinimumHeight());
            }
        }

        //Check against minimum height and width
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //Calculate constraints
        int offset = 0;
        if (drawable != null) offset = (int) drawable.calculateOffset();
        if (offset < 0) offset = 0;

        final int boundsLeft = this.getPaddingLeft() + offset;
        final int boundsTop = this.getPaddingTop() + offset;
        final int boundsRight = this.getMeasuredWidth() - this.getPaddingRight() - offset;
        final int boundsBottom = this.getMeasuredHeight() - this.getPaddingBottom() - offset;

        final int width = boundsRight - boundsLeft;
        final int height = boundsBottom - boundsTop;

        final int centerX = boundsLeft + (width / 2);
        final int centerY = boundsTop + (height / 2);

        //Calculate layout
        int count = getChildCount();

        for (int pos = 0; pos < count; pos++) {
            View child = getChildAt(pos);
            if (child == null) continue;

            int leftMargin = 0;
            int topMargin = 0;
            int rightMargin = 0;
            int bottomMargin = 0;

            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params != null && params instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) params;
                leftMargin = lp.leftMargin;
                topMargin = lp.topMargin;
                rightMargin = lp.rightMargin;
                bottomMargin = lp.bottomMargin;
            } else if (params != null && params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                leftMargin = lp.leftMargin;
                topMargin = lp.topMargin;
                rightMargin = lp.rightMargin;
                bottomMargin = lp.bottomMargin;
            }

            if (child.getVisibility() != GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                int offsetX = (width - childWidth) / 2;
                int offsetY = (height - childHeight) / 2;

                int offsetLeft = Math.max(0, leftMargin - offsetX);
                int offsetTop = Math.max(0, topMargin - offsetY);
                int offsetRight = Math.min(0, offsetX - rightMargin);
                int offsetBottom = Math.min(0, offsetY - bottomMargin);

                int centerPosX = centerX + offsetLeft + offsetRight;
                int centerPosY = centerY + offsetTop + offsetBottom;

                int childLeft = centerPosX - (childWidth / 2);
                int childTop = centerPosY - (childHeight / 2);
                int childRight = centerPosX + (childWidth / 2);
                int childBottom = centerPosY + (childHeight / 2);

                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }

        //Calculate attributes
        onCalculateAttributes();
    }

    protected void onCalculateAttributes() {
        //Set attributes
        if (arrowTarget != 0 && pointer == null)
            setPointer(new Pointer(arrowTarget, true));
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params != null && params instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            if (params instanceof LayoutParams)
                return new LayoutParams((LayoutParams) params);
            else if (params instanceof ViewGroup.MarginLayoutParams)
                return new LayoutParams((ViewGroup.MarginLayoutParams) params);
            else
                return new LayoutParams((ViewGroup.LayoutParams) params);
        }
        return null;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public static class Pointer extends Vector2 implements ViewTreeObserver.OnGlobalLayoutListener {
        private boolean attached;

        private View contentView;

        private int pointerId;
        private View pointerView;

        public Pointer() {
            super();
            update();
        }

        public Pointer(Pointer src) {
            super(src);
            update();
        }

        public Pointer(int x, int y) {
            super(x, y);
            update();
        }

        public Pointer(double x, double y) {
            super(x, y);
            update();
        }

        public Pointer(@IdRes int pointerId, boolean attach) {
            super();
            this.pointerId = pointerId;
            update();
            if (attach) attach();
        }

        public Pointer(View pointerView, boolean attach) {
            super();
            this.pointerView = pointerView;
            update();
            if (attach) attach();
        }

        private synchronized void setContentView(View contentView, boolean attach) {
            detach();
            this.contentView = contentView;
            update();
            if (attach) attach();
        }

        public synchronized void setPointerId(int pointerId, boolean attach) {
            detach();
            this.pointerId = pointerId;
            this.pointerView = null;
            update();
            if (attach) attach();
        }

        public synchronized void setPointerView(View pointerView, boolean attach) {
            detach();
            this.pointerView = pointerView;
            update();
            if (attach) attach();
        }

        public synchronized boolean update() {
            boolean changed = false;

            if (pointerView != null) {
                pointerId = pointerView.getId();
            } else if (contentView != null) {
                try {
                    View rootView = contentView.getRootView();
                    pointerView = rootView.findViewById(pointerId);
                } catch (Exception e) {
                }
            }

            if (contentView != null && pointerView != null) {
                try {
                    Rect contentViewBounds = getViewBounds(contentView);
                    Rect pointerViewBounds = getViewBounds(pointerView);

                    if (contentViewBounds != null && pointerViewBounds != null) {
                        Line line = new Line(
                                contentViewBounds.left,
                                contentViewBounds.top,
                                pointerViewBounds.centerX(),
                                pointerViewBounds.centerY()
                        );

                        Vector2 nextPointer = line.getDelta();
                        changed |= !isEqualTo(nextPointer);

                        set(nextPointer);
                    } else {
                        Vector2 nextPointer = new Vector2(0d, 0d);
                        changed |= !isEqualTo(nextPointer);

                        set(nextPointer);
                    }
                } catch (Exception e) {
                }
            }

            if (changed && contentView != null && contentView instanceof BubbleCardView) {
                BubbleCardView cardView = (BubbleCardView) contentView;
                cardView.redraw(true);
            }

            return changed;
        }

        @Override
        public synchronized void onGlobalLayout() {
            try {
                update();
            } catch (Exception e) {
            }
        }

        public synchronized boolean isAttached() {
            return attached;
        }

        public synchronized void attach() {
            attached = true;

            try {
                if (contentView != null)
                    contentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
                if (pointerView != null)
                    pointerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            } catch (Exception e) {
            }
        }

        public synchronized void detach() {
            attached = false;

            try {
                if (contentView != null)
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (pointerView != null)
                    pointerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } catch (Exception e) {
            }
        }

        public static final Rect getViewBounds(View view) {
            if (view == null)
                return null;

            try {
                int[] pos = new int[2];
                view.getLocationOnScreen(pos);

                int width = view.getWidth();
                int height = view.getHeight();

                if (width <= 0)
                    width = view.getMeasuredWidth();
                if (height <= 0)
                    height = view.getMeasuredHeight();

                if (width <= 0 || height <= 0)
                    return null;

                return new Rect(pos[0],
                        pos[1],
                        pos[0] + width,
                        pos[1] + height);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}