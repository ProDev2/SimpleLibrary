package com.simplelib.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

import com.simplelib.R;
import com.simplelib.helper.TimeHelper;

import java.util.concurrent.atomic.AtomicLong;

public class CircularImageView extends AppCompatImageView {
    //Static variables
    private static final float DEFAULT_IMAGE_OFFSET_DP = 0f;
    private static final float DEFAULT_CROP_OFFSET_DP = 15f;
    private static final float DEFAULT_DASH_OFFSET_DP = 2f;

    private static final float DEFAULT_TRANSLATION_SECTION = 1f;

    private static final float DEFAULT_IMAGE_TRANSLATION_DP = 10f;
    private static final float DEFAULT_CROP_TRANSLATION_DP = 10f;
    private static final float DEFAULT_DASH_TRANSLATION_DP = 10f;

    private static final float DEFAULT_IMAGE_TRANSLATION_ROTATION = 0f;
    private static final float DEFAULT_CROP_TRANSLATION_ROTATION = 0f;
    private static final float DEFAULT_DASH_TRANSLATION_ROTATION = 0f;

    private static final float DEFAULT_IMAGE_ROTATION = 0f;
    private static final float DEFAULT_DASH_ROTATION = 0f;

    private static final int DEFAULT_IMAGE_COLOR = 0x00000000;
    private static final float DEFAULT_IMAGE_CROP = 1f;

    private static final boolean DEFAULT_SHOW_DASHES = true;
    private static final float DEFAULT_DASH_VISIBILITY = 1f;

    private static final float DEFAULT_DASH_START_ANGLE = 0f;
    private static final float DEFAULT_DASH_SWEEP_ANGLE = 360f;

    private static final boolean DEFAULT_REVERSE_DASHES = false;

    private static final int DEFAULT_DASH_COUNT = 4;
    private static final float DEFAULT_DASH_SPACING_ANGLE = 20;
    private static final int DEFAULT_DASH_COLOR = 0xFFFFFFFF;
    private static final float DEFAULT_DASH_STROKE_WIDTH_DP = 6f;

    private static final boolean DEFAULT_DISTRIBUTE_DASHES_EVENLY = false;

    private static final float DEFAULT_DASH_SHADOW_RADIUS_DP = 1f;
    private static final int DEFAULT_DASH_SHADOW_COLOR = 0x25000000;

    //Variables (attributes)
    private float imageOffset;
    private float cropOffset;
    private float dashOffset;

    private float translationSection;

    private float imageTranslation;
    private float cropTranslation;
    private float dashTranslation;

    private float imageTranslationRotation;
    private float cropTranslationRotation;
    private float dashTranslationRotation;

    private float imageRotation;
    private float dashRotation;

    private int imageColor;
    private float imageCrop;

    private boolean showDashes;
    private float dashVisibility;

    private float dashStartAngle;
    private float dashSweepAngle;

    private boolean reverseDashes;

    private int dashCount;
    private float dashSpacingAngle;
    private int dashColor;
    private float dashStrokeWidth;

    private boolean distributeDashesEvenly;

    private float dashShadowRadius;
    private int dashShadowColor;

    //Variables
    private float extraDashes = 0f;

    private float imageTranslationRotationVelocity = 0f;
    private float cropTranslationRotationVelocity = 0f;
    private float dashTranslationRotationVelocity = 0f;

    private float imageRotationVelocity = 0f;
    private float dashRotationVelocity = 0f;

    //Draw
    protected Path imagePath;

    protected Paint dashPaint;
    protected PathEffect dashPathEffect;

    //Holder
    private AtomicLong lastTimeImageTranslationRotation = new AtomicLong(TimeHelper.NO_TIME);
    private AtomicLong lastTimeCropTranslationRotation = new AtomicLong(TimeHelper.NO_TIME);
    private AtomicLong lastTimeDashTranslationRotation = new AtomicLong(TimeHelper.NO_TIME);

    private AtomicLong lastTimeImageRotation = new AtomicLong(TimeHelper.NO_TIME);
    private AtomicLong lastTimeDashRotation = new AtomicLong(TimeHelper.NO_TIME);

    public CircularImageView(Context context) {
        super(context);
        initialize(null, 0, 0);
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs, 0, 0);
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, 0);
    }

    public CircularImageView(Context context,
                             @Nullable AttributeSet attrs,
                             int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //Use defaults
        imageOffset = dpToPx(DEFAULT_IMAGE_OFFSET_DP);
        cropOffset = dpToPx(DEFAULT_CROP_OFFSET_DP);
        dashOffset = dpToPx(DEFAULT_DASH_OFFSET_DP);

        translationSection = DEFAULT_TRANSLATION_SECTION;

        imageTranslation = dpToPx(DEFAULT_IMAGE_TRANSLATION_DP);
        cropTranslation = dpToPx(DEFAULT_CROP_TRANSLATION_DP);
        dashTranslation = dpToPx(DEFAULT_DASH_TRANSLATION_DP);

        imageTranslationRotation = DEFAULT_IMAGE_TRANSLATION_ROTATION;
        cropTranslationRotation = DEFAULT_CROP_TRANSLATION_ROTATION;
        dashTranslationRotation = DEFAULT_DASH_TRANSLATION_ROTATION;

        imageRotation = DEFAULT_IMAGE_ROTATION;
        dashRotation = DEFAULT_DASH_ROTATION;

        imageColor = DEFAULT_IMAGE_COLOR;
        imageCrop = DEFAULT_IMAGE_CROP;

        showDashes = DEFAULT_SHOW_DASHES;
        dashVisibility = DEFAULT_DASH_VISIBILITY;

        dashStartAngle = DEFAULT_DASH_START_ANGLE;
        dashSweepAngle = DEFAULT_DASH_SWEEP_ANGLE;

        reverseDashes = DEFAULT_REVERSE_DASHES;

        dashCount = DEFAULT_DASH_COUNT;
        dashSpacingAngle = DEFAULT_DASH_SPACING_ANGLE;
        dashColor = DEFAULT_DASH_COLOR;
        dashStrokeWidth = dpToPx(DEFAULT_DASH_STROKE_WIDTH_DP);

        distributeDashesEvenly = DEFAULT_DISTRIBUTE_DASHES_EVENLY;

        dashShadowRadius = dpToPx(DEFAULT_DASH_SHADOW_RADIUS_DP);
        dashShadowColor = DEFAULT_DASH_SHADOW_COLOR;

        //Fetch styled attributes
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CircularImageView,
                    defStyleAttr,
                    defStyleRes);

            //Flags
            int flags = attributes.getInt(R.styleable.CircularImageView_iv_flags, 0b0);

            //Check for style flags (3 bytes)
            if ((flags & 0b111) == 0b001)
                imageCrop = 0.0f;
            else if ((flags & 0b111) == 0b011)
                imageCrop = 0.35f;
            else if ((flags & 0b111) == 0b111)
                imageCrop = 1.0f;

            //Check for dash flags (2 bytes)
            if ((flags & 0b11_000) == 0b01_000)
                showDashes = false;
            else if ((flags & 0b11_000) == 0b11_000)
                showDashes = true;

            //Check for rotation flags (3 bytes)
            if ((flags & 0b111_00000) != 0b000_00000) {
                imageRotationVelocity = 0.0f;
                dashRotationVelocity = 0.0f;
            }
            if ((flags & 0b011_00000) == 0b011_00000)
                imageRotationVelocity = 90.0f;
            if ((flags & 0b101_00000) == 0b101_00000)
                dashRotationVelocity = 90.0f;

            //Check for translation flags (4 bytes)
            if ((flags & 0b1111_00000000) != 0b0000_00000000) {
                imageTranslationRotationVelocity = 0.0f;
                cropTranslationRotationVelocity = 0.0f;
                dashTranslationRotationVelocity = 0.0f;
            }
            if ((flags & 0b0011_00000000) == 0b0011_00000000)
                imageTranslationRotationVelocity = 90.0f;
            if ((flags & 0b0101_00000000) == 0b0101_00000000)
                cropTranslationRotationVelocity = 90.0f;
            if ((flags & 0b1001_00000000) == 0b1001_00000000)
                dashTranslationRotationVelocity = 90.0f;

            //Check for offset flags (4 bytes)
            if ((flags & 0b1111_000000000000) != 0b0000_000000000000) {
                imageOffset = 0.0f;
                cropOffset = 0.0f;
                dashOffset = 0.0f;
            }
            if ((flags & 0b0011_000000000000) == 0b0011_000000000000)
                imageOffset = dpToPx(DEFAULT_IMAGE_OFFSET_DP);
            if ((flags & 0b0101_000000000000) == 0b0101_000000000000)
                cropOffset = dpToPx(DEFAULT_CROP_OFFSET_DP);
            if ((flags & 0b1001_000000000000) == 0b1001_000000000000)
                dashOffset = dpToPx(DEFAULT_DASH_OFFSET_DP);

            //Variables (attributes)
            imageOffset = attributes.getDimension(R.styleable.CircularImageView_iv_imageOffset,
                    imageOffset);
            cropOffset = attributes.getDimension(R.styleable.CircularImageView_iv_cropOffset,
                    cropOffset);
            dashOffset = attributes.getDimension(R.styleable.CircularImageView_iv_dashOffset,
                    dashOffset);

            translationSection = attributes.getFloat(R.styleable.CircularImageView_iv_translationSection,
                    translationSection);

            imageTranslation = attributes.getDimension(R.styleable.CircularImageView_iv_imageTranslation,
                    imageTranslation);
            cropTranslation = attributes.getDimension(R.styleable.CircularImageView_iv_cropTranslation,
                    cropTranslation);
            dashTranslation = attributes.getDimension(R.styleable.CircularImageView_iv_dashTranslation,
                    dashTranslation);

            imageTranslationRotation = attributes.getFloat(R.styleable.CircularImageView_iv_imageTranslationRotation,
                    imageTranslationRotation);
            cropTranslationRotation = attributes.getFloat(R.styleable.CircularImageView_iv_cropTranslationRotation,
                    cropTranslationRotation);
            dashTranslationRotation = attributes.getFloat(R.styleable.CircularImageView_iv_dashTranslationRotation,
                    dashTranslationRotation);

            imageRotation = attributes.getFloat(R.styleable.CircularImageView_iv_imageRotation,
                    imageRotation);
            dashRotation = attributes.getFloat(R.styleable.CircularImageView_iv_dashRotation,
                    dashRotation);

            imageColor = attributes.getColor(R.styleable.CircularImageView_iv_imageColor,
                    imageColor);
            imageCrop = attributes.getFloat(R.styleable.CircularImageView_iv_imageCrop, imageCrop);

            showDashes = attributes.getBoolean(R.styleable.CircularImageView_iv_showDashes,
                    showDashes);
            dashVisibility = attributes.getFloat(R.styleable.CircularImageView_iv_dashVisibility,
                    dashVisibility);

            dashStartAngle = attributes.getFloat(R.styleable.CircularImageView_iv_dashStartAngle,
                    dashStartAngle);
            dashSweepAngle = attributes.getFloat(R.styleable.CircularImageView_iv_dashSweepAngle,
                    dashSweepAngle);

            reverseDashes = attributes.getBoolean(R.styleable.CircularImageView_iv_reverseDashes,
                    reverseDashes);

            dashCount = attributes.getInteger(R.styleable.CircularImageView_iv_dashCount,
                    dashCount);
            dashSpacingAngle = attributes.getFloat(R.styleable.CircularImageView_iv_dashSpacingAngle,
                    dashSpacingAngle);
            dashColor = attributes.getColor(R.styleable.CircularImageView_iv_dashColor, dashColor);
            dashStrokeWidth = attributes.getDimension(R.styleable.CircularImageView_iv_dashStrokeWidth,
                    dashStrokeWidth);

            distributeDashesEvenly = attributes.getBoolean(R.styleable.CircularImageView_iv_distributeDashesEvenly,
                    distributeDashesEvenly);

            dashShadowRadius = attributes.getDimension(R.styleable.CircularImageView_iv_dashShadowRadius,
                    dashShadowRadius);
            dashShadowColor = attributes.getColor(R.styleable.CircularImageView_iv_dashShadowColor,
                    dashShadowColor);

            //Variables
            extraDashes = attributes.getFloat(R.styleable.CircularImageView_iv_extraDashes,
                    extraDashes);

            imageTranslationRotationVelocity = attributes.getFloat(R.styleable.CircularImageView_iv_imageTranslationRotationVelocity,
                    imageTranslationRotationVelocity);
            cropTranslationRotationVelocity = attributes.getFloat(R.styleable.CircularImageView_iv_cropTranslationRotationVelocity,
                    cropTranslationRotationVelocity);
            dashTranslationRotationVelocity = attributes.getFloat(R.styleable.CircularImageView_iv_dashTranslationRotationVelocity,
                    dashTranslationRotationVelocity);

            imageRotationVelocity = attributes.getFloat(R.styleable.CircularImageView_iv_imageRotationVelocity,
                    imageRotationVelocity);
            dashRotationVelocity = attributes.getFloat(R.styleable.CircularImageView_iv_dashRotationVelocity,
                    dashRotationVelocity);

            //Recycle attributes
            attributes.recycle();
        }

        //Draw
        updateDashPathEffect();
        updateDashPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        handleUpdate();

        drawImage(canvas);
        drawDashes(canvas);
    }

    protected void handleUpdate() {
        if (!isEnabled())
            return;

        boolean callUpdate = false;

        float translationSection = getTranslationSection();
        if (translationSection < 0) translationSection = -translationSection;
        float fullTranslationSection = translationSection != 0f ? 360f / translationSection : 0f;

        float imageTranslationRotationVelocity = getImageTranslationRotationVelocity();
        if (imageTranslationRotationVelocity != 0f) {
            float imageTranslationDeltaRotation = TimeHelper.getDelta(
                    lastTimeImageTranslationRotation,
                    imageTranslationRotationVelocity);
            this.imageTranslationRotation += imageTranslationDeltaRotation;

            if (translationSection != 0f) {
                float section = this.imageTranslationRotation / translationSection;
                if (section >= fullTranslationSection)
                    this.imageTranslationRotation -= fullTranslationSection;
                else if (section <= -fullTranslationSection)
                    this.imageTranslationRotation += fullTranslationSection;
            }

            callUpdate = true;
        } else {
            lastTimeImageTranslationRotation.set(TimeHelper.NO_TIME);
        }

        float cropTranslationRotationVelocity = getCropTranslationRotationVelocity();
        if (cropTranslationRotationVelocity != 0f) {
            float cropTranslationDeltaRotation = TimeHelper.getDelta(lastTimeCropTranslationRotation,
                    cropTranslationRotationVelocity);
            this.cropTranslationRotation += cropTranslationDeltaRotation;

            if (translationSection != 0f) {
                float section = this.cropTranslationRotation / translationSection;
                if (section >= fullTranslationSection)
                    this.cropTranslationRotation -= fullTranslationSection;
                else if (section <= -fullTranslationSection)
                    this.cropTranslationRotation += fullTranslationSection;
            }

            callUpdate = true;
        } else {
            lastTimeCropTranslationRotation.set(TimeHelper.NO_TIME);
        }

        float dashTranslationRotationVelocity = getDashTranslationRotationVelocity();
        if (dashTranslationRotationVelocity != 0f) {
            float dashTranslationDeltaRotation = TimeHelper.getDelta(lastTimeDashTranslationRotation,
                    dashTranslationRotationVelocity);
            this.dashTranslationRotation += dashTranslationDeltaRotation;

            if (translationSection != 0f) {
                float section = this.dashTranslationRotation / translationSection;
                if (section >= fullTranslationSection)
                    this.dashTranslationRotation -= fullTranslationSection;
                else if (section <= -fullTranslationSection)
                    this.dashTranslationRotation += fullTranslationSection;
            }

            callUpdate = true;
        } else {
            lastTimeDashTranslationRotation.set(TimeHelper.NO_TIME);
        }

        float imageRotationVelocity = getImageRotationVelocity();
        if (imageRotationVelocity != 0f) {
            float imageDeltaRotation = TimeHelper.getDelta(lastTimeImageRotation,
                    imageRotationVelocity);
            this.imageRotation += imageDeltaRotation;

            if (this.imageRotation >= 360f)
                this.imageRotation -= 360f;
            else if (this.imageRotation <= -360f)
                this.imageRotation += 360f;

            callUpdate = true;
        } else {
            lastTimeImageRotation.set(TimeHelper.NO_TIME);
        }

        float dashRotationVelocity = getDashRotationVelocity();
        if (dashRotationVelocity != 0f) {
            float dashDeltaRotation = TimeHelper.getDelta(lastTimeDashRotation,
                    dashRotationVelocity);
            this.dashRotation += dashDeltaRotation;

            if (this.dashRotation >= 360f)
                this.dashRotation -= 360f;
            else if (this.dashRotation <= -360f)
                this.dashRotation += 360f;

            callUpdate = true;
        } else {
            lastTimeDashRotation.set(TimeHelper.NO_TIME);
        }

        if (callUpdate)
            invalidate();
    }

    @SuppressLint("WrongCall")
    protected void drawImage(Canvas canvas) {
        if (canvas == null)
            return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        if (width <= 0 || height <= 0)
            return;

        float imageOffset = getImageOffset();
        float cropOffset = getCropOffset();

        float imageTranslationRotation = getImageTranslationRotation();
        float cropTranslationRotation = getCropTranslationRotation();

        float translationSection = getTranslationSection();
        float imageTranslation = getImageTranslation();
        float cropTranslation = getCropTranslation();

        if (translationSection != 0f) {
            float imageTranslationCycle = (float) Math.sin(Math.toRadians((imageTranslationRotation /
                    translationSection) - 90f));
            float cropTranslationCycle = (float) Math.sin(Math.toRadians((cropTranslationRotation /
                    translationSection) - 90f));

            imageOffset -= imageTranslation * ((imageTranslationCycle / 2f) + 0.5f);
            cropOffset -= cropTranslation * ((cropTranslationCycle / 2f) + 0.5f);
        }

        float imageRotation = getImageRotation();

        int imageColor = getImageColor();

        //Draw image
        int imageState = canvas.save();

        boolean skipDraw = false;

        float imageCrop = getImageCrop();
        if (!skipDraw && imageCrop > 0f) {
            if (imagePath == null) {
                imagePath = new Path();
            }

            RectF pathBounds = new RectF(cropOffset * imageCrop,
                    cropOffset * imageCrop,
                    width - (cropOffset * imageCrop),
                    height - (cropOffset * imageCrop));

            float pathBoundsWidth = pathBounds.width();
            float pathBoundsHeight = pathBounds.height();

            float circleSize = Math.min(pathBoundsWidth, pathBoundsHeight);

            float resizeX = pathBoundsWidth - circleSize;
            float resizeY = pathBoundsHeight - circleSize;

            float insetX = resizeX * imageCrop;
            float insetY = resizeY * imageCrop;

            pathBounds.inset(insetX / 2f, insetY / 2f);

            if (pathBounds.width() > 0f && pathBounds.height() > 0) {
                float cornerRadius = (circleSize / 2f) * imageCrop;

                imagePath.reset();
                imagePath.addRoundRect(pathBounds,
                        cornerRadius,
                        cornerRadius,
                        Path.Direction.CW);
                imagePath.close();

                canvas.clipPath(imagePath);
            } else {
                skipDraw = true;
            }
        }

        if (!skipDraw && Color.alpha(imageColor) > 0) {
            canvas.drawColor(imageColor);
        }

        if (!skipDraw && imageRotation != 0f) {
            canvas.rotate(imageRotation, centerX, centerY);
        }

        if (!skipDraw && imageOffset != 0f) {
            float scaledWidth = (float) width - (imageOffset * 2f);
            float scaledHeight = (float) height - (imageOffset * 2f);
            canvas.translate(imageOffset, imageOffset);
            canvas.scale(scaledWidth / (float) width, scaledHeight / (float) height);
        }

        if (!skipDraw) {
            super.onDraw(canvas);
        }

        canvas.restoreToCount(imageState);
    }

    protected void drawDashes(Canvas canvas) {
        if (canvas == null)
            return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int size = Math.min(width, height);
        int halfSize = size / 2;

        int centerX = width / 2;
        int centerY = height / 2;

        if (width <= 0 || height <= 0)
            return;

        boolean showDashes = isShowDashes();
        float dashVisibility = getDashVisibility();
        if (!showDashes || dashVisibility <= 0f)
            return;

        //Draw dashes
        int dashCount = getDashCount();
        float extraDashes = getExtraDashes();
        float dashSpacingAngle = getDashSpacingAngle();

        boolean distributeDashesEvenly = isDistributeDashesEvenly();
        if (!distributeDashesEvenly) {
            int fullExtraDashes = (int) Math.floor((double) extraDashes);
            dashCount += (float) fullExtraDashes;
            extraDashes -= (float) fullExtraDashes;
        }

        float totalDashes = dashCount + extraDashes;

        if (totalDashes <= 0f)
            return;

        //Calculate dashes and spaces
        float dashStartAngle = getDashStartAngle();
        float dashSweepAngle = getDashSweepAngle();

        float extraRatio = totalDashes != 0f ? extraDashes / totalDashes : 0f;
        float ratio = 1f - extraRatio;

        int extraDashCount = (int) (extraDashes >=
                0f ? Math.ceil((double) extraDashes) : Math.floor((double) extraDashes));
        int totalDashCount = dashCount + extraDashCount;

        int totalDashCountPos = totalDashCount >= 0 ? totalDashCount : -totalDashCount;

        float fullSpacingAngle = totalDashCountPos > 1 ? (float) dashCount * dashSpacingAngle : 0f;
        if (totalDashCountPos == 2)
            fullSpacingAngle *= totalDashes + (totalDashes >= 0f ? -1f : +1f);
        if (totalDashCountPos == 1)
            fullSpacingAngle += ((dashSweepAngle * ratio) - fullSpacingAngle) * (1f - totalDashes);
        float fullDashAngle = (dashSweepAngle * ratio) - fullSpacingAngle;
        float spacingAngle = dashCount != 0 ? fullSpacingAngle / (float) dashCount : 0f;
        float dashAngle = dashCount != 0 ? fullDashAngle / (float) dashCount : 0f;

        float fullExtraSpacingAngle = totalDashCountPos > 1 ? (float) extraDashCount *
                dashSpacingAngle : 0f;
        if (totalDashCountPos == 2)
            fullExtraSpacingAngle *= totalDashes + (totalDashes >= 0f ? -1f : +1f);
        if (totalDashCountPos == 1)
            fullExtraSpacingAngle += ((dashSweepAngle * extraRatio) - fullExtraSpacingAngle) *
                    (1f - totalDashes);
        float fullExtraDashAngle = (dashSweepAngle * extraRatio) - fullExtraSpacingAngle;
        float extraSpacingAngle = extraDashCount != 0 ? fullExtraSpacingAngle /
                (float) extraDashCount : 0f;
        float extraDashAngle = extraDashCount != 0 ? fullExtraDashAngle /
                (float) extraDashCount : 0f;

        float halfSpacingAngle = spacingAngle / 2f;

        //Draw dashes
        boolean reverseDashes = isReverseDashes();

        float dashOffset = getDashOffset();

        float dashTranslationRotation = getDashTranslationRotation();

        float translationSection = getTranslationSection();
        float dashTranslation = getDashTranslation();

        if (translationSection != 0f) {
            float dashTranslationCycle = (float) Math.sin(Math.toRadians((dashTranslationRotation /
                    translationSection) - 90f));

            dashOffset -= dashTranslation * ((dashTranslationCycle / 2f) + 0.5f);
        }

        float dashRotation = getDashRotation();

        int dashState = canvas.save();
        if (!reverseDashes)
            canvas.rotate(dashStartAngle + dashRotation + halfSpacingAngle, centerX, centerY);
        else
            canvas.rotate(dashStartAngle + dashRotation - halfSpacingAngle, centerX, centerY);

        RectF bounds = new RectF(centerX - halfSize + dashOffset,
                centerY - halfSize + dashOffset,
                centerX + halfSize - dashOffset,
                centerY + halfSize - dashOffset);

        if (!reverseDashes) {
            for (int pos = 1; pos <= dashCount; pos++) {
                if (dashAngle > 0f)
                    canvas.drawArc(bounds, -90f, dashAngle, false, dashPaint);
                canvas.rotate(dashAngle + spacingAngle, centerX, centerY);
            }
            for (int pos = 1; pos <= extraDashCount; pos++) {
                if (extraDashAngle > 0f)
                    canvas.drawArc(bounds, -90f, extraDashAngle, false, dashPaint);
                canvas.rotate(extraDashAngle + extraSpacingAngle, centerX, centerY);
            }
        } else {
            for (int pos = 1; pos <= dashCount; pos++) {
                if (dashAngle > 0f)
                    canvas.drawArc(bounds, -90f - dashAngle, dashAngle, false, dashPaint);
                canvas.rotate(-dashAngle - spacingAngle, centerX, centerY);
            }
            for (int pos = 1; pos <= extraDashCount; pos++) {
                if (extraDashAngle > 0f)
                    canvas.drawArc(bounds, -90f - extraDashAngle, extraDashAngle, false, dashPaint);
                canvas.rotate(-extraDashAngle - extraSpacingAngle, centerX, centerY);
            }
        }

        canvas.restoreToCount(dashState);
    }

    protected void updateDashPaint() {
        if (dashPaint == null) {
            dashPaint = new Paint();
        }

        int dashColor = getDashColor();
        float dashStrokeWidth = getDashStrokeWidth();

        dashPaint.setColor(dashColor);
        dashPaint.setStrokeWidth(dashStrokeWidth);
        dashPaint.setDither(true);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeJoin(Paint.Join.ROUND);
        dashPaint.setStrokeCap(Paint.Cap.ROUND);
        dashPaint.setAntiAlias(true);

        float dashVisibility = getDashVisibility();
        if (dashVisibility < 0f) dashVisibility = 0f;
        if (dashVisibility > 1f) dashVisibility = 1f;

        int alpha = Color.alpha(dashColor);
        dashPaint.setAlpha((int) (alpha * dashVisibility));

        if (dashPathEffect == null) {
            updateDashPathEffect();
        }
        dashPaint.setPathEffect(dashPathEffect);

        updateDashShadowLayer();
    }

    protected void updateDashShadowLayer() {
        if (dashPaint == null)
            throw new NullPointerException("Initialize dash paint first");

        float dashVisibility = getDashVisibility();
        if (dashVisibility < 0f) dashVisibility = 0f;
        if (dashVisibility > 1f) dashVisibility = 1f;

        float dashShadowRadius = getDashShadowRadius();
        if (dashShadowRadius <= 0f || dashVisibility <= 0f) {
            dashPaint.clearShadowLayer();
        } else {
            int dashShadowColor = getDashShadowColor();

            int alpha = Color.alpha(dashShadowColor);
            int red = Color.red(dashShadowColor);
            int green = Color.green(dashShadowColor);
            int blue = Color.blue(dashShadowColor);

            int shadowColor = Color.argb((int) ((float) alpha * dashVisibility),
                    red,
                    green,
                    blue);
            dashPaint.setShadowLayer(dashShadowRadius, 0f, 0f, shadowColor);
        }
    }

    protected void updateDashPathEffect() {
        //dashPathEffect = new DashPathEffect(new float[] {200, 250}, 20);
    }

    public float getImageOffset() {
        return imageOffset;
    }

    public void setImageOffset(float imageOffset) {
        boolean changed = this.imageOffset != imageOffset;
        this.imageOffset = imageOffset;
        if (changed)
            invalidate();
    }

    public float getCropOffset() {
        return cropOffset;
    }

    public void setCropOffset(float cropOffset) {
        boolean changed = this.cropOffset != cropOffset;
        this.cropOffset = cropOffset;
        if (changed)
            invalidate();
    }

    public float getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(float dashOffset) {
        boolean changed = this.dashOffset != dashOffset;
        this.dashOffset = dashOffset;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 1f)
    public float getTranslationSection() {
        return translationSection;
    }

    public void setTranslationSection(@FloatRange(from = 0f, to = 1f) float translationSection) {
        boolean changed = this.translationSection != translationSection;
        this.translationSection = translationSection;
        if (changed)
            invalidate();
    }

    public float getImageTranslation() {
        return imageTranslation;
    }

    public void setImageTranslation(float imageTranslation) {
        boolean changed = this.imageTranslation != imageTranslation;
        this.imageTranslation = imageTranslation;
        if (changed)
            invalidate();
    }

    public float getCropTranslation() {
        return cropTranslation;
    }

    public void setCropTranslation(float cropTranslation) {
        boolean changed = this.cropTranslation != cropTranslation;
        this.cropTranslation = cropTranslation;
        if (changed)
            invalidate();
    }

    public float getDashTranslation() {
        return dashTranslation;
    }

    public void setDashTranslation(float dashTranslation) {
        boolean changed = this.dashTranslation != dashTranslation;
        this.dashTranslation = dashTranslation;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getImageTranslationRotation() {
        return imageTranslationRotation;
    }

    public void setImageTranslationRotation(@FloatRange(from = 0f,
            to = 360f) float imageTranslationRotation) {
        boolean changed = this.imageTranslationRotation != imageTranslationRotation;
        this.imageTranslationRotation = imageTranslationRotation;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getCropTranslationRotation() {
        return cropTranslationRotation;
    }

    public void setCropTranslationRotation(@FloatRange(from = 0f,
            to = 360f) float cropTranslationRotation) {
        boolean changed = this.cropTranslationRotation != cropTranslationRotation;
        this.cropTranslationRotation = cropTranslationRotation;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashTranslationRotation() {
        return dashTranslationRotation;
    }

    public void setDashTranslationRotation(@FloatRange(from = 0f,
            to = 360f) float dashTranslationRotation) {
        boolean changed = this.dashTranslationRotation != dashTranslationRotation;
        this.dashTranslationRotation = dashTranslationRotation;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getImageRotation() {
        return imageRotation;
    }

    public void setImageRotation(@FloatRange(from = 0f, to = 360f) float imageRotation) {
        boolean changed = this.imageRotation != imageRotation;
        this.imageRotation = imageRotation;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashRotation() {
        return dashRotation;
    }

    public void setDashRotation(@FloatRange(from = 0f, to = 360f) float dashRotation) {
        boolean changed = this.dashRotation != dashRotation;
        this.dashRotation = dashRotation;
        if (changed)
            invalidate();
    }

    @ColorInt
    public int getImageColor() {
        return imageColor;
    }

    public void setImageColor(@ColorInt int imageColor) {
        boolean changed = this.imageColor != imageColor;
        this.imageColor = imageColor;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 1f)
    public float getImageCrop() {
        return imageCrop;
    }

    public void setImageCrop(@FloatRange(from = 0f, to = 1f) float imageCrop) {
        boolean changed = this.imageCrop != imageCrop;
        this.imageCrop = imageCrop;
        if (changed)
            invalidate();
    }

    public boolean isShowDashes() {
        return showDashes;
    }

    public void setShowDashes(boolean showDashes) {
        boolean changed = this.showDashes != showDashes;
        this.showDashes = showDashes;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 1f)
    public float getDashVisibility() {
        return dashVisibility;
    }

    public void setDashVisibility(@FloatRange(from = 0f, to = 1f) float dashVisibility) {
        boolean changed = this.dashVisibility != dashVisibility;
        this.dashVisibility = dashVisibility;
        if (changed) {
            updateDashPaint();
            invalidate();
        }
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashStartAngle() {
        return dashStartAngle;
    }

    public void setDashStartAngle(@FloatRange(from = 0f, to = 360f) float dashStartAngle) {
        boolean changed = this.dashStartAngle != dashStartAngle;
        this.dashStartAngle = dashStartAngle;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashSweepAngle() {
        return dashSweepAngle;
    }

    public void setDashSweepAngle(@FloatRange(from = 0f, to = 360f) float dashSweepAngle) {
        boolean changed = this.dashSweepAngle != dashSweepAngle;
        this.dashSweepAngle = dashSweepAngle;
        if (changed)
            invalidate();
    }

    public boolean isReverseDashes() {
        return reverseDashes;
    }

    public void setReverseDashes(boolean reverseDashes) {
        boolean changed = this.reverseDashes != reverseDashes;
        this.reverseDashes = reverseDashes;
        if (changed)
            invalidate();
    }

    public int getDashCount() {
        return dashCount;
    }

    public void setDashCount(int dashCount) {
        boolean changed = this.dashCount != dashCount;
        this.dashCount = dashCount;
        if (changed)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashSpacingAngle() {
        return dashSpacingAngle;
    }

    public void setDashSpacingAngle(@FloatRange(from = 0f, to = 360f) float dashSpacingAngle) {
        boolean changed = this.dashSpacingAngle != dashSpacingAngle;
        this.dashSpacingAngle = dashSpacingAngle;
        if (changed)
            invalidate();
    }

    @ColorInt
    public int getDashColor() {
        return dashColor;
    }

    public void setDashColor(@ColorInt int dashColor) {
        boolean changed = this.dashColor != dashColor;
        this.dashColor = dashColor;
        if (changed) {
            updateDashPaint();
            invalidate();
        }
    }

    public float getDashStrokeWidth() {
        return dashStrokeWidth;
    }

    public void setDashStrokeWidth(float dashStrokeWidth) {
        boolean changed = this.dashStrokeWidth != dashStrokeWidth;
        this.dashStrokeWidth = dashStrokeWidth;
        if (changed) {
            updateDashPaint();
            invalidate();
        }
    }

    public boolean isDistributeDashesEvenly() {
        return distributeDashesEvenly;
    }

    public void setDistributeDashesEvenly(boolean distributeDashesEvenly) {
        boolean changed = this.distributeDashesEvenly != distributeDashesEvenly;
        this.distributeDashesEvenly = distributeDashesEvenly;
        if (changed)
            invalidate();
    }

    public float getDashShadowRadius() {
        return dashShadowRadius;
    }

    public void setDashShadowRadius(float dashShadowRadius) {
        boolean changed = this.dashShadowRadius != dashShadowRadius;
        this.dashShadowRadius = dashShadowRadius;
        if (changed) {
            updateDashPaint();
            invalidate();
        }
    }

    @ColorInt
    public int getDashShadowColor() {
        return dashShadowColor;
    }

    public void setDashShadowColor(@ColorInt int dashShadowColor) {
        boolean changed = this.dashShadowColor != dashShadowColor;
        this.dashShadowColor = dashShadowColor;
        if (changed) {
            updateDashPaint();
            invalidate();
        }
    }

    public float getExtraDashes() {
        return extraDashes;
    }

    public void setExtraDashes(float extraDashes, boolean update) {
        boolean changed = this.extraDashes != extraDashes;
        this.extraDashes = extraDashes;
        if (changed && update)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getImageTranslationRotationVelocity() {
        return imageTranslationRotationVelocity;
    }

    public void setImageTranslationRotationVelocity(@FloatRange(from = 0f,
            to = 360f) float imageTranslationRotationVelocity,
                                                    boolean update) {
        boolean changed = this.imageTranslationRotationVelocity != imageTranslationRotationVelocity;
        this.imageTranslationRotationVelocity = imageTranslationRotationVelocity;
        if (changed && update)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getCropTranslationRotationVelocity() {
        return cropTranslationRotationVelocity;
    }

    public void setCropTranslationRotationVelocity(@FloatRange(from = 0f,
            to = 360f) float cropTranslationRotationVelocity,
                                                   boolean update) {
        boolean changed = this.cropTranslationRotationVelocity != cropTranslationRotationVelocity;
        this.cropTranslationRotationVelocity = cropTranslationRotationVelocity;
        if (changed && update)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashTranslationRotationVelocity() {
        return dashTranslationRotationVelocity;
    }

    public void setDashTranslationRotationVelocity(@FloatRange(from = 0f,
            to = 360f) float dashTranslationRotationVelocity,
                                                   boolean update) {
        boolean changed = this.dashTranslationRotationVelocity != dashTranslationRotationVelocity;
        this.dashTranslationRotationVelocity = dashTranslationRotationVelocity;
        if (changed && update)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getImageRotationVelocity() {
        return imageRotationVelocity;
    }

    public void setImageRotationVelocity(@FloatRange(from = 0f,
            to = 360f) float imageRotationVelocity,
                                         boolean update) {
        boolean changed = this.imageRotationVelocity != imageRotationVelocity;
        this.imageRotationVelocity = imageRotationVelocity;
        if (changed && update)
            invalidate();
    }

    @FloatRange(from = 0f, to = 360f)
    public float getDashRotationVelocity() {
        return dashRotationVelocity;
    }

    public void setDashRotationVelocity(@FloatRange(from = 0f,
            to = 360f) float dashRotationVelocity,
                                        boolean update) {
        boolean changed = this.dashRotationVelocity != dashRotationVelocity;
        this.dashRotationVelocity = dashRotationVelocity;
        if (changed && update)
            invalidate();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);

        //Variables (attributes)
        savedState.imageOffset = imageOffset;
        savedState.cropOffset = cropOffset;
        savedState.dashOffset = dashOffset;

        savedState.translationSection = translationSection;

        savedState.imageTranslation = imageTranslation;
        savedState.cropTranslation = cropTranslation;
        savedState.dashTranslation = dashTranslation;

        savedState.imageTranslationRotation = imageTranslationRotation;
        savedState.cropTranslationRotation = cropTranslationRotation;
        savedState.dashTranslationRotation = dashTranslationRotation;

        savedState.imageRotation = imageRotation;
        savedState.dashRotation = dashRotation;

        savedState.imageColor = imageColor;
        savedState.imageCrop = imageCrop;

        savedState.showDashes = showDashes;
        savedState.dashVisibility = dashVisibility;

        savedState.dashStartAngle = dashStartAngle;
        savedState.dashSweepAngle = dashSweepAngle;

        savedState.reverseDashes = reverseDashes;

        savedState.dashCount = dashCount;
        savedState.dashSpacingAngle = dashSpacingAngle;
        savedState.dashColor = dashColor;
        savedState.dashStrokeWidth = dashStrokeWidth;

        savedState.distributeDashesEvenly = distributeDashesEvenly;

        savedState.dashShadowRadius = dashShadowRadius;
        savedState.dashShadowColor = dashShadowColor;

        //Variables
        savedState.extraDashes = extraDashes;

        savedState.imageTranslationRotationVelocity = imageTranslationRotationVelocity;
        savedState.cropTranslationRotationVelocity = cropTranslationRotationVelocity;
        savedState.dashTranslationRotationVelocity = dashTranslationRotationVelocity;

        savedState.imageRotationVelocity = imageRotationVelocity;
        savedState.dashRotationVelocity = dashRotationVelocity;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null)
            return;

        SavedState savedState = (SavedState) state;

        super.onRestoreInstanceState(savedState.getSuperState());

        //Variables (attributes)
        imageOffset = savedState.imageOffset;
        cropOffset = savedState.cropOffset;
        dashOffset = savedState.dashOffset;

        translationSection = savedState.translationSection;

        imageTranslation = savedState.imageTranslation;
        cropTranslation = savedState.cropTranslation;
        dashTranslation = savedState.dashTranslation;

        imageTranslationRotation = savedState.imageTranslationRotation;
        cropTranslationRotation = savedState.cropTranslationRotation;
        dashTranslationRotation = savedState.dashTranslationRotation;

        imageRotation = savedState.imageRotation;
        dashRotation = savedState.dashRotation;

        imageColor = savedState.imageColor;
        imageCrop = savedState.imageCrop;

        showDashes = savedState.showDashes;
        dashVisibility = savedState.dashVisibility;

        dashStartAngle = savedState.dashStartAngle;
        dashSweepAngle = savedState.dashSweepAngle;

        reverseDashes = savedState.reverseDashes;

        dashCount = savedState.dashCount;
        dashSpacingAngle = savedState.dashSpacingAngle;
        dashColor = savedState.dashColor;
        dashStrokeWidth = savedState.dashStrokeWidth;

        distributeDashesEvenly = savedState.distributeDashesEvenly;

        dashShadowRadius = savedState.dashShadowRadius;
        dashShadowColor = savedState.dashShadowColor;

        //Variables
        extraDashes = savedState.extraDashes;

        imageTranslationRotationVelocity = savedState.imageTranslationRotationVelocity;
        cropTranslationRotationVelocity = savedState.cropTranslationRotationVelocity;
        dashTranslationRotationVelocity = savedState.dashTranslationRotationVelocity;

        imageRotationVelocity = savedState.imageRotationVelocity;
        dashRotationVelocity = savedState.dashRotationVelocity;

        //Update
        updateDashPaint();
        invalidate();
    }

    protected static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    private static class SavedState extends BaseSavedState {
        //Variables (attributes)
        public float imageOffset;
        public float cropOffset;
        public float dashOffset;

        public float translationSection;

        public float imageTranslation;
        public float cropTranslation;
        public float dashTranslation;

        public float imageTranslationRotation;
        public float cropTranslationRotation;
        public float dashTranslationRotation;

        public float imageRotation;
        public float dashRotation;

        public int imageColor;
        public float imageCrop;

        public boolean showDashes;
        public float dashVisibility;

        public float dashStartAngle;
        public float dashSweepAngle;

        public boolean reverseDashes;

        public int dashCount;
        public float dashSpacingAngle;
        public int dashColor;
        public float dashStrokeWidth;

        public boolean distributeDashesEvenly;

        public float dashShadowRadius;
        public int dashShadowColor;

        //Variables
        public float extraDashes;

        public float imageTranslationRotationVelocity;
        public float cropTranslationRotationVelocity;
        public float dashTranslationRotationVelocity;

        public float imageRotationVelocity;
        public float dashRotationVelocity;

        //Saved State
        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            Class<?> cls = getClass();
            ClassLoader loader = cls != null ? cls.getClassLoader() : null;

            readFromParcel(in, loader);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);

            if (loader == null) {
                Class<?> cls = getClass();
                loader = cls != null ? cls.getClassLoader() : loader;
            }

            readFromParcel(in, loader);
        }

        private void readFromParcel(Parcel in, ClassLoader loader) {
            if (in == null)
                return;

            //Variables (attributes)
            imageOffset = in.readFloat();
            cropOffset = in.readFloat();
            dashOffset = in.readFloat();

            translationSection = in.readFloat();

            imageTranslation = in.readFloat();
            cropTranslation = in.readFloat();
            dashTranslation = in.readFloat();

            imageTranslationRotation = in.readFloat();
            cropTranslationRotation = in.readFloat();
            dashTranslationRotation = in.readFloat();

            imageRotation = in.readFloat();
            dashRotation = in.readFloat();

            imageColor = in.readInt();
            imageCrop = in.readFloat();

            showDashes = in.readInt() == 1;
            dashVisibility = in.readFloat();

            dashStartAngle = in.readFloat();
            dashSweepAngle = in.readFloat();

            reverseDashes = in.readInt() == 1;

            dashCount = in.readInt();
            dashSpacingAngle = in.readFloat();
            dashColor = in.readInt();
            dashStrokeWidth = in.readFloat();

            distributeDashesEvenly = in.readInt() == 1;

            dashShadowRadius = in.readFloat();
            dashShadowColor = in.readInt();

            //Variables
            extraDashes = in.readFloat();

            imageTranslationRotationVelocity = in.readFloat();
            cropTranslationRotationVelocity = in.readFloat();
            dashTranslationRotationVelocity = in.readFloat();

            imageRotationVelocity = in.readFloat();
            dashRotationVelocity = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            if (out == null)
                return;

            //Variables (attributes)
            out.writeFloat(imageOffset);
            out.writeFloat(cropOffset);
            out.writeFloat(dashOffset);

            out.writeFloat(translationSection);

            out.writeFloat(imageTranslation);
            out.writeFloat(cropTranslation);
            out.writeFloat(dashTranslation);

            out.writeFloat(imageTranslationRotation);
            out.writeFloat(cropTranslationRotation);
            out.writeFloat(dashTranslationRotation);

            out.writeFloat(imageRotation);
            out.writeFloat(dashRotation);

            out.writeInt(imageColor);
            out.writeFloat(imageCrop);

            out.writeInt(showDashes ? 1 : 0);
            out.writeFloat(dashVisibility);

            out.writeFloat(dashStartAngle);
            out.writeFloat(dashSweepAngle);

            out.writeInt(reverseDashes ? 1 : 0);

            out.writeInt(dashCount);
            out.writeFloat(dashSpacingAngle);
            out.writeInt(dashColor);
            out.writeFloat(dashStrokeWidth);

            out.writeInt(distributeDashesEvenly ? 1 : 0);

            out.writeFloat(dashShadowRadius);
            out.writeInt(dashShadowColor);

            //Variables
            out.writeFloat(extraDashes);

            out.writeFloat(imageTranslationRotationVelocity);
            out.writeFloat(cropTranslationRotationVelocity);
            out.writeFloat(dashTranslationRotationVelocity);

            out.writeFloat(imageRotationVelocity);
            out.writeFloat(dashRotationVelocity);
        }

        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return new SavedState(source, loader);
                } else {
                    return new SavedState(source);
                }
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}