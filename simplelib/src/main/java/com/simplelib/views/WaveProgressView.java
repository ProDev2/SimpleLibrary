package com.simplelib.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class WaveProgressView extends View implements Runnable {
    //Constants
    private static final int DEFAULT_UPDATE_RATE = 10;

    private static final float DEFAULT_WAVE_WIDTH_DP = 100f;
    private static final float DEFAULT_WAVE_HEIGHT_DP = 20f;
    private static final float DEFAULT_WAVE_SPEED_DP = 2.5f;

    private static final int DEFAULT_WAVE_COLOR = 0xFF4285F4;

    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_MAX_PROGRESS = 100;

    private static final float DEFAULT_ANIMATION_SPEED = 0.2f;

    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_TEXT_SIZE = 41;

    //Values
    private Bitmap image;

    private boolean running;
    private Handler handler;

    private int updateRate;

    private Bitmap waveImage;
    private Canvas waveCanvas;

    private Path wavePath;
    private Paint wavePaint;

    private float waveDistance;
    private float waveWidth;
    private float waveHeight;
    private float waveSpeed;

    private float progress;
    private float maxProgress;

    private float posRealY;
    private float posCurrentY;
    private float posAnimSpeed;

    private String text;
    private Paint textPaint;

    public WaveProgressView(Context context) {
        this(context, null, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (getBackground() != null) {
            image = getBitmapFromDrawable(getBackground());

            try {
                setBackground(null);
            } catch (Exception e) {
            }
        }

        wavePath = new Path();
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        //Setup values
        setUpdateRate(DEFAULT_UPDATE_RATE);

        setWaveSizeInDp(DEFAULT_WAVE_WIDTH_DP, DEFAULT_WAVE_HEIGHT_DP);
        setWaveSpeedInDp(DEFAULT_WAVE_SPEED_DP);

        int waveColor = fetchColorPrimary();
        if (waveColor == -1)
            waveColor = DEFAULT_WAVE_COLOR;
        setWaveColor(waveColor);

        setProgress(DEFAULT_PROGRESS);
        setMaxProgress(DEFAULT_MAX_PROGRESS);

        setPosAnimSpeed(DEFAULT_ANIMATION_SPEED);

        setTextColor(DEFAULT_TEXT_COLOR);
        setTextSizeInSp(DEFAULT_TEXT_SIZE);
    }

    public void start() {
        start(-1);
    }

    public void start(int delay) {
        stop();

        try {
            resetWavePos();

            if (handler == null)
                handler = new Handler();

            running = true;
            if (delay < 0)
                delay = 200;
            if (delay > 0)
                handler.postDelayed(this, delay);
            else
                run();
        } catch (Exception e) {
        }
    }

    public void stop() {
        try {
            running = false;
            if (handler != null)
                handler.removeCallbacks(this);
        } catch (Exception e) {
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void resetWavePos() {
        if (getHeight() > 0)
            posRealY = posCurrentY = getHeight();
    }

    @Override
    public void run() {
        invalidate();

        if (running && handler != null)
            handler.postDelayed(this, updateRate);
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setDrawable(Drawable drawable) {
        this.image = getBitmapFromDrawable(drawable);
    }

    public void setResource(int id) {
        this.image = getBitmapFromDrawable(ContextCompat.getDrawable(getContext(), id));
    }

    public void setUpdateRate(int updateRate) {
        this.updateRate = updateRate;
    }

    public void setWaveSize(float waveWidth, float waveHeight) {
        this.waveWidth = waveWidth;
        this.waveHeight = waveHeight;
    }

    public void setWaveSizeInDp(float waveWidthDp, float waveHeightDp) {
        this.waveWidth = dpToPx(waveWidthDp);
        this.waveHeight = dpToPx(waveHeightDp);
    }

    public void setWaveSpeed(float waveSpeed) {
        this.waveSpeed = waveSpeed;
    }

    public void setWaveSpeedInDp(float waveSpeedDp) {
        this.waveSpeed = dpToPx(waveSpeedDp);
    }

    public void setWaveColor(int waveColor) {
        if (wavePaint != null)
            wavePaint.setColor(waveColor);
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setPosAnimSpeed(float posAnimSpeed) {
        this.posAnimSpeed = posAnimSpeed;
    }

    public void setText(String mCurrentText) {
        this.text = mCurrentText;
    }

    public void setTextColor(int textColor) {
        if (textPaint != null)
            textPaint.setColor(textColor);
    }

    public void setTextSize(float textSize) {
        if (textPaint != null)
            textPaint.setTextSize(textSize);
    }

    public void setTextSizeInSp(float textSizeSp) {
        if (textPaint != null)
            textPaint.setTextSize(spToPx(textSizeSp));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        posRealY = posCurrentY = height;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (image != null) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            int oversize = getOversize(width, height, imgWidth, imgHeight);
            imgWidth -= oversize;
            imgHeight -= oversize;

            if (image.getWidth() != imgWidth && image.getHeight() != imgHeight) {
                Bitmap previousImage = image;
                image = Bitmap.createScaledBitmap(image, imgWidth, imgHeight, false);

                try {
                    if (previousImage != null && image != null)
                        previousImage.recycle();
                } catch (Exception e) {
                }
            }

            Bitmap progressImage = createImage(image);

            int imgPosX = (width / 2) - (image.getWidth() / 2);
            int imgPosY = (height / 2) - (image.getHeight() / 2);
            canvas.drawBitmap(progressImage, imgPosX, imgPosY, null);
        }
    }

    private Bitmap createImage(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (!(waveImage != null && waveCanvas != null && waveImage.getWidth() == width && waveImage.getHeight() == height)) {
            if (waveImage != null)
                waveImage.recycle();

            waveImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            waveCanvas = new Canvas(waveImage);
        } else {
            waveCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        posRealY = height * (maxProgress - progress) / maxProgress;

        float moveBy = (posCurrentY - posRealY) * posAnimSpeed;
        posCurrentY = moveNumberTowards(posCurrentY, posRealY, moveBy);

        wavePath.reset();
        wavePath.moveTo(-waveDistance, posCurrentY);

        float entireWaveWith = waveWidth * 2;
        int waveNum = (int) ((width + entireWaveWith) / entireWaveWith) + 1;

        int multiplier = 0;
        for (int i = 0; i < waveNum; i++) {
            wavePath.quadTo(waveWidth * (multiplier + 0.5f) - waveDistance, posCurrentY - (waveHeight / 2), waveWidth * (multiplier + 1f) - waveDistance, posCurrentY);
            wavePath.quadTo(waveWidth * (multiplier + 1.5f) - waveDistance, posCurrentY + (waveHeight / 2), waveWidth * (multiplier + 2f) - waveDistance, posCurrentY);
            multiplier += 2;
        }
        waveDistance += waveSpeed;
        waveDistance = waveDistance % entireWaveWith;

        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
        wavePath.close();
        waveCanvas.drawPath(wavePath, wavePaint);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        waveCanvas.drawBitmap(image, 0, 0, paint);

        if (text != null && text.length() > 0)
            waveCanvas.drawText(text, width / 2, height / 2, textPaint);

        return waveImage;
    }

    private int getOversize(int width, int height, int widthTo, int heightTo) {
        int oversizeX = widthTo - width;
        int oversizeY = heightTo - height;

        return Math.max(oversizeX, oversizeY);
    }

    private float moveNumberTowards(float moveNumber, float moveTowards, float moveBy) {
        if (moveBy < 0)
            moveBy = -moveBy;

        if (moveTowards > moveNumber) {
            moveNumber += moveBy;
            if (moveTowards < moveNumber)
                moveNumber = moveTowards;
        } else if (moveTowards < moveNumber) {
            moveNumber -= moveBy;
            if (moveTowards > moveNumber)
                moveNumber = moveTowards;
        }
        return moveNumber;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable)
                return ((BitmapDrawable) drawable).getBitmap();

            try {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                return bitmap;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    private float spToPx(float sp) {
        return sp * Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    private int spToPx(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    private int fetchColorPrimary() {
        try {
            TypedValue typedValue = new TypedValue();
            TypedArray typedArray = getContext().obtainStyledAttributes(typedValue.data, new int[] {android.support.design.R.attr.colorPrimary});

            int color = typedArray.getColor(0, 0);
            typedArray.recycle();

            return color;
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}