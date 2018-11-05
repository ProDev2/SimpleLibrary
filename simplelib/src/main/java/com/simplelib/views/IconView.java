package com.simplelib.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class IconView extends AppCompatImageView {
    private Bitmap image;
    private Canvas imageCanvas;

    private Camera camera;
    private Matrix matrix;
    private Paint paint;

    private int backgroundColor;

    private float transX, transY, transZ;
    private float rotX, rotY, rotZ;

    public IconView(Context context) {
        super(context);
        init();
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (matrix == null)
            matrix = new Matrix();

        if (camera == null)
            camera = new Camera();

        if (paint == null) {
            paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paint.setDither(true);
        }

        try {
            int color = Color.TRANSPARENT;
            Drawable background = getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            this.backgroundColor = color;
        } catch (Exception e) {
        }
    }

    public void setIconColorFilter(ColorFilter colorFilter) {
        try {
            if (paint != null)
                paint.setColorFilter(colorFilter);
        } catch (Exception e) {
        }
    }

    public void removeIconColorFilter() {
        try {
            if (paint != null)
                paint.setColorFilter(null);
        } catch (Exception e) {
        }
    }

    public int getIconBackgroundColor() {
        return backgroundColor;
    }

    public void setIconBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void removeIconBackgroundColor() {
        removeIconBackgroundColor(false);
    }

    public void removeIconBackgroundColor(boolean fetchDefault) {
        try {
            int color = Color.TRANSPARENT;
            if (fetchDefault) {
                Drawable background = getBackground();
                if (background instanceof ColorDrawable)
                    color = ((ColorDrawable) background).getColor();
            }
            this.backgroundColor = color;
        } catch (Exception e) {
        }
    }

    public void setTransX(float transX) {
        this.transX = transX;
    }

    public void setTransY(float transY) {
        this.transY = transY;
    }

    public void setTransZ(float transZ) {
        this.transZ = transZ;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getTransX() {
        return transX;
    }

    public float getTransY() {
        return transY;
    }

    public float getTransZ() {
        return transZ;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void redraw() {
        try {
            if (Looper.getMainLooper().getThread() == Thread.currentThread())
                invalidate();
            else
                postInvalidate();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (image == null || (image != null && (image.getWidth() != canvas.getWidth() || image.getHeight() != canvas.getHeight()))) {
            try {
                if (image != null)
                    image.recycle();
            } catch (Exception e) {
            }

            image = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            imageCanvas = new Canvas(image);
        } else {
            image.eraseColor(Color.TRANSPARENT);
        }
        imageCanvas.drawColor(backgroundColor);

        super.onDraw(imageCanvas);

        camera.save();
        camera.translate(transX, transY, transZ);
        camera.rotateX(rotX);
        camera.rotateY(rotY);
        camera.rotateZ(rotZ);
        camera.getMatrix(matrix);
        camera.restore();

        int width = imageCanvas.getWidth();
        int height = imageCanvas.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        canvas.drawBitmap(image, matrix, paint);
    }

    private PointF calculatePoint(float x, float y) {
        float[] pts = new float[] {x, y};
        if (matrix != null)
            matrix.mapPoints(pts);
        return new PointF(pts[0], pts[1]);
    }
}
