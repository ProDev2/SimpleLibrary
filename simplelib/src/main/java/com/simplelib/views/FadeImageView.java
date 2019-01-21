package com.simplelib.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.simplelib.tools.ImageTools;

import java.util.ArrayList;

public class FadeImageView extends View {
    private static final long DEFAULT_ANIMATION_DURATION = 1500;

    private ValueAnimator animator;
    private float value;

    private Paint alphaPaint;

    private Bitmap cachedImage;
    private Bitmap currentImage;

    public FadeImageView(Context context) {
        super(context);
        init();
    }

    public FadeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FadeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FadeImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setupAnimator();

        alphaPaint = new Paint();
    }

    private void setupAnimator() {
        value = 1f;

        try {
            if (animator == null) {
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(DEFAULT_ANIMATION_DURATION);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        value = animation.getAnimatedFraction();
                        if (value < 0f) value = 0f;
                        if (value > 1f) value = 1f;

                        invalidate();
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    private Bitmap baseImage;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        baseImage = cachedImage;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            if (baseImage != null && !baseImage.isRecycled())
                                baseImage.recycle();
                        } catch (Exception e) {
                        }
                        baseImage = null;
                    }
                });
            } else {
                animator.cancel();
            }
            animator.setFloatValues(0f, 1f);
        } catch (Exception e) {
        }
    }

    public ValueAnimator getAnimator() {
        return animator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        if (animator != null) animator.setInterpolator(interpolator);
    }

    public void setDuration(long duration) {
        if (animator != null) animator.setDuration(duration);
    }

    public void animateToColor(int color) {
        setColor(color, true);
    }

    public void animateToBlurredImageBitmap(Bitmap image, float sampleSize, float blurRadius) {
        setBlurredImageBitmap(image, sampleSize, blurRadius, true);
    }

    public void animateTo(Bitmap image) {
        setImageBitmap(image, true, true);
    }

    public void animateTo(Bitmap image, boolean copy) {
        setImageBitmap(image, copy, true);
    }

    public void setColor(int color, boolean animate) {
        try {
            int width = getWidth();
            int height = getHeight();

            if (width <= 0) width = 100;
            if (height <= 0) height = 100;

            setImageBitmap(ImageTools.createImage(width, height, color), false, animate);
        } catch (Exception e) {
        }
    }

    public void setBlurredImageBitmap(Bitmap image, float sampleSize, float blurRadius, final boolean animate) {
        ImageTools.ImageBlur.blur(getContext(), new ImageTools.ImageBlur.OnBlurListener() {
            @Override
            public void onFinish(ArrayList<Bitmap> images) {
                if (images != null && images.size() > 0) {
                    setImageBitmap(images.get(0), false, animate);
                }
            }
        }, sampleSize, blurRadius, image);
    }

    public void setImageBitmap(Bitmap image, boolean animate) {
        setImageBitmap(image, true, animate);
    }

    public void setImageBitmap(Bitmap image, boolean copy, boolean animate) {
        if (image != null && copy)
            image = image.copy(Bitmap.Config.ARGB_8888, true);

        try {
            if (cachedImage != null && !cachedImage.isRecycled())
                cachedImage.recycle();
            cachedImage = null;
        } catch (Exception e) {
        }

        cachedImage = currentImage;
        currentImage = image;

        setupAnimator();

        if (animate) {
            if (animator != null)
                animator.start();
        } else {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 0;
        int desiredHeight = 0;

        if (currentImage != null) {
            int imageWidth = currentImage.getWidth();
            int imageHeight = currentImage.getHeight();

            if (imageWidth > 0 && imageHeight > 0) {
                desiredWidth = imageWidth;
                desiredHeight = imageHeight;
            }
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY)
            width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST)
            width = Math.min(desiredWidth, widthSize);
        else
            width = desiredWidth;

        if (heightMode == MeasureSpec.EXACTLY)
            height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST)
            height = Math.min(desiredHeight, heightSize);
        else
            height = desiredHeight;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (cachedImage != null) {
            if (currentImage != null) {
                alphaPaint.setAlpha(255);
            } else {
                int alpha = (int) (255f - (255f * value));
                if (alpha < 0) alpha = 0;
                if (alpha > 255) alpha = 255;

                alphaPaint.setAlpha(alpha);
            }

            Rect cachedImageBounds = getBounds(cachedImage, width, height);
            if (cachedImageBounds != null)
                canvas.drawBitmap(cachedImage, null, cachedImageBounds, alphaPaint);
        }

        if (currentImage != null) {
            int alpha = (int) (255f * value);
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;

            alphaPaint.setAlpha(alpha);

            Rect currentImageBounds = getBounds(currentImage, width, height);
            if (currentImageBounds != null)
                canvas.drawBitmap(currentImage, null, currentImageBounds, alphaPaint);
        }
    }

    private Rect getBounds(Bitmap image, int reqWidth, int reqHeight) {
        if (image == null || reqWidth <= 0 || reqHeight <= 0)
            return null;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int oversize = ImageTools.getCropOversize(imageWidth, imageHeight, reqWidth, reqHeight);

        int boundsPosX = (reqWidth / 2) - (imageWidth / 2) + (oversize / 2);
        int boundsPosY = (reqHeight / 2) - (imageHeight / 2) + (oversize / 2);

        int boundsWidth = imageWidth - oversize;
        int boundsHeight = imageHeight - oversize;

        if (boundsWidth <= 0 || boundsHeight <= 0) return null;

        return new Rect(boundsPosX, boundsPosY, boundsPosX + boundsWidth, boundsPosY + boundsHeight);
    }

    public void destroy() {
        try {
            if (cachedImage != null && !cachedImage.isRecycled())
                cachedImage.recycle();
        } catch (Exception e) {
        }

        try {
            if (currentImage != null && !currentImage.isRecycled())
                currentImage.recycle();
        } catch (Exception e) {
        }
    }
}
