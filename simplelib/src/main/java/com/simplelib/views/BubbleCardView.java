package com.simplelib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.simplelib.R;
import com.simplelib.math.Vector2;
import com.simplelib.tools.Tools;
import com.simplelib.views.drawable.BubbleCardDrawable;

public class BubbleCardView extends RelativeLayout {
    //Static variables
    private static final float DEFAULT_CORNER_RADIUS_DP = -1f;
    private static final float DEFAULT_CONTENT_PADDING_DP = 5f;

    private static final int DEFAULT_ARROW_TARGET_ID = 0;
    private static final float DEFAULT_ARROW_SIZE_DP = 10f;
    private static final float DEFAULT_ARROW_CORNER_RADIUS_DP = 5f;

    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    //Variables (attributes)
    private float cornerRadius;
    private float contentPadding;

    private int arrowTarget;
    private float arrowSize;
    private float arrowCornerRadius;

    private int backgroundColor;

    //Variables
    private Pointer pointer;

    //Draw
    private BubbleCardDrawable drawable;

    public BubbleCardView(Context context) {
        super(context);
        initialize(null, 0);
    }

    public BubbleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0);
    }

    public BubbleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs, defStyleAttr);
    }

    private void initialize(AttributeSet attrs, int defStyleAttr) {
        //Setup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                //TODO: Not working

                setClipToOutline(true);
                setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            } catch (Exception e) {
            }
        }

        //Use defaults
        cornerRadius = Tools.dpToPx(DEFAULT_CORNER_RADIUS_DP);
        contentPadding = Tools.dpToPx(DEFAULT_CONTENT_PADDING_DP);

        arrowTarget = DEFAULT_ARROW_TARGET_ID;
        arrowSize = Tools.dpToPx(DEFAULT_ARROW_SIZE_DP);
        arrowCornerRadius = Tools.dpToPx(DEFAULT_ARROW_CORNER_RADIUS_DP);

        backgroundColor = DEFAULT_BACKGROUND_COLOR;

        //Fetch styled attributes
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleCardView, defStyleAttr, 0);

            cornerRadius = attributes.getDimension(R.styleable.BubbleCardView_bcv_cornerRadius, cornerRadius);
            contentPadding = attributes.getDimension(R.styleable.BubbleCardView_bcv_contentPadding, contentPadding);

            arrowTarget = attributes.getResourceId(R.styleable.BubbleCardView_bcv_arrowTarget, arrowTarget);
            arrowSize = attributes.getDimension(R.styleable.BubbleCardView_bcv_arrowSize, arrowSize);
            arrowCornerRadius = attributes.getDimension(R.styleable.BubbleCardView_bcv_arrowCornerRadius, arrowCornerRadius);

            backgroundColor = attributes.getColor(R.styleable.BubbleCardView_bcv_backgroundColor, backgroundColor);

            attributes.recycle();
        }

        //Set attributes
        if (arrowTarget != 0)
            setPointer(new Pointer(arrowTarget));
        setContentPadding(contentPadding);

        //Set background
        drawable = new BubbleCardDrawable(backgroundColor, cornerRadius, arrowSize, arrowCornerRadius);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }

        //Update
        update();
    }

    public void setColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        update();
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        update();
    }

    public void setContentPadding(float contentPadding) {
        this.contentPadding = contentPadding;

        //TODO: Set padding to fit background drawable (including arrow)
        setPadding((int) contentPadding, (int) contentPadding, (int) contentPadding, (int) contentPadding);
    }

    public void setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
        update();
    }

    public void setArrowCornerRadius(float arrowCornerRadius) {
        this.arrowCornerRadius = arrowCornerRadius;
        update();
    }

    public void setPointer(Pointer pointer) {
        boolean changed = this.pointer != pointer;
        this.pointer = pointer;
        if (pointer != null) {
            pointer.setContentView(this);
        }
        if (changed) update();
    }

    public void update() {
        if (drawable != null) {
            drawable.setColor(backgroundColor);

            drawable.setCornerRadius(cornerRadius);

            drawable.setArrowSize(arrowSize);
            drawable.setArrowCornerRadius(arrowCornerRadius);

            drawable.setArrowTarget(pointer);
        }
    }

    public static class Pointer extends Vector2 {
        private View contentView;

        private int pointerId;
        private View pointerView;

        public Pointer() {
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

        public Pointer(@IdRes int pointerId) {
            super();
            this.pointerId = pointerId;
            update();
        }

        public Pointer(View pointerView) {
            super();
            this.pointerView = pointerView;
            update();
        }

        private void setContentView(View contentView) {
            this.contentView = contentView;
            update();
        }

        public void update() {
            if (pointerView != null) {
                pointerId = pointerView.getId();
            } else if (contentView != null) {
                try {
                    View rootView = contentView.getRootView();
                    if (rootView != null)
                        contentView = rootView;

                    pointerView = contentView.findViewById(pointerId);
                } catch (Exception e) {
                }
            }

            if (pointerView != null) {
                try {
                    Rect pointerViewBounds = new Rect();
                    pointerView.getGlobalVisibleRect(pointerViewBounds);

                    set(pointerViewBounds.centerX(), pointerViewBounds.centerY());
                } catch (Exception e) {
                }
            }
        }
    }
}
