package com.simplelib.math;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class Vector4 {
    public Vector2 pos, size;

    public float rotation;
    public Vector2 rotationPoint;

    public Vector4(Vector4 vector) {
        vector.applyTo(this);
    }

    public Vector4(Vector2 pos, Vector2 size) {
        this.pos = pos;
        this.size = size;

        this.rotationPoint = new Vector2();
        this.setRotationPointToCenter();
    }

    public Vector4(float x, float y, float width, float height) {
        this.pos = new Vector2(x, y);
        this.size = new Vector2(width, height);

        this.rotationPoint = new Vector2();
        this.setRotationPointToCenter();
    }

    public Vector4(int x, int y, int width, int height) {
        this.pos = new Vector2(x, y);
        this.size = new Vector2(width, height);

        this.rotationPoint = new Vector2();
        this.setRotationPointToCenter();
    }

    public Vector4 setPos(float x, float y) {
        pos.setX(x);
        pos.setY(y);
        return this;
    }

    public Vector2 getPos() {
        return pos;
    }

    public float getX() {
        return pos.getX();
    }

    public float getY() {
        return pos.getY();
    }

    public int getXAsInt() {
        return pos.getXAsInt();
    }

    public int getYAsInt() {
        return pos.getYAsInt();
    }

    public Vector4 moveTo(float x, float y) {
        pos.moveTo(x, y);
        return this;
    }

    public Vector4 moveBy(float x, float y) {
        pos.moveBy(x, y);
        return this;
    }

    public Vector4 moveTo(int x, int y) {
        pos.moveTo(x, y);
        return this;
    }

    public Vector4 moveBy(int x, int y) {
        pos.moveBy(x, y);
        return this;
    }

    public Vector4 setSize(float width, float height) {
        size.setX(width);
        size.setY(height);
        return this;
    }

    public Vector4 setSize(int width, int height) {
        size.setX(width);
        size.setY(height);
        return this;
    }

    public Vector2 getSize() {
        return size;
    }

    public float getWidth() {
        return size.getX();
    }

    public float getHeight() {
        return size.getY();
    }

    public int getWidthAsInt() {
        return size.getXAsInt();
    }

    public int getHeightAsInt() {
        return size.getYAsInt();
    }

    public Vector4 resizeTo(float x, float y) {
        size.moveTo(x, y);
        return this;
    }

    public Vector4 resizeBy(float x, float y) {
        size.moveBy(x, y);
        return this;
    }

    public Vector4 resizeTo(int x, int y) {
        size.moveTo(x, y);
        return this;
    }

    public Vector4 resizeBy(int x, int y) {
        size.moveBy(x, y);
        return this;
    }

    public Vector4 zoom(float x, float y) {
        moveBy(-x, -y);
        resizeBy(x * 2, y * 2);
        return this;
    }

    public Vector4 zoom(int x, int y) {
        moveBy(-x, -y);
        resizeBy(x * 2, y * 2);
        return this;
    }

    public float getRotation() {
        return rotation;
    }

    public Vector4 setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public Vector4 setRotation(int rotation) {
        this.rotation = rotation;
        return this;
    }

    public float getRotationAsInt() {
        return (int) rotation;
    }

    public Vector4 setRotationPoint(float x, float y) {
        rotationPoint.setX(x);
        rotationPoint.setY(y);
        return this;
    }

    public Vector4 setRotationPoint(int x, int y) {
        rotationPoint.setX(x);
        rotationPoint.setY(y);
        return this;
    }

    public Vector4 setRotationPointToCenter() {
        getCenter().applyTo(getRotationPoint());
        return this;
    }

    public Vector2 getRotationPoint() {
        return rotationPoint;
    }

    public float getRotPointX() {
        return rotationPoint.getX();
    }

    public float getRotPointY() {
        return rotationPoint.getY();
    }

    public int getRotPointXAsInt() {
        return rotationPoint.getXAsInt();
    }

    public int getRotPointYAsInt() {
        return rotationPoint.getYAsInt();
    }

    public Vector2 getCenter() {
        try {
            return new Vector2(size.getX() / 2, size.getY() / 2);
        } catch (Exception e) {
            return new Vector2();
        }
    }

    public float getHalfWidth() {
        return getCenter().getX();
    }

    public float getHalfHeight() {
        return getCenter().getY();
    }

    public int getHalfWidthAsInt() {
        return getCenter().getXAsInt();
    }

    public int getHalfHeightAsInt() {
        return getCenter().getYAsInt();
    }

    public Vector4 applyTo(Vector4 vector) {
        if (vector != null) {
            vector.pos = pos.copy();
            vector.size = size.copy();

            vector.rotation = rotation;
            vector.rotationPoint = rotationPoint.copy();
        }
        return this;
    }

    public Vector4 copy() {
        return new Vector4(this);
    }

    public Vector2 getRotatedStart() {
        PointRotator rotator = new PointRotator(pos, pos.copy().add(rotationPoint));
        return rotator.rotate(rotation);
    }

    public Vector2 getRotatedCenter() {
        PointRotator rotator = new PointRotator(pos.copy().add(getCenter()), pos.copy().add(rotationPoint));
        return rotator.rotate(rotation);
    }

    public Vector2 getRotatedEnd() {
        PointRotator rotator = new PointRotator(pos.copy().add(size), pos.copy().add(rotationPoint));
        return rotator.rotate(rotation);
    }

    public Vector2 getRelativePos(Vector2 point) {
        PointRotator rotator = new PointRotator(point, pos.copy().add(rotationPoint));
        Vector2 rotatedPoint = rotator.rotate(-rotation);
        return rotatedPoint.subtract(pos);
    }

    public Vector2 getAbsolutePos(Vector2 point) {
        point = point.copy().add(pos);
        PointRotator rotator = new PointRotator(point, pos.copy().add(rotationPoint));
        return rotator.rotate(rotation);
    }

    public boolean containsPoint(Vector2 point) {
        PointRotator rotator = new PointRotator(point, pos.copy().add(rotationPoint));
        point = rotator.rotate(-rotation);

        boolean inX = point.getX() >= getX() && point.getX() <= (getX() + getWidth());
        boolean inY = point.getY() >= getY() && point.getY() <= (getY() + getHeight());

        return inX && inY;
    }

    public Vector4 getFullRect() {
        if (getRotation() != 0) {
            Vector2[] posList = new Vector2[] {
                    getAbsolutePos(new Vector2(0, 0)),
                    getAbsolutePos(new Vector2(getWidth(), 0)),
                    getAbsolutePos(new Vector2(0, getHeight())),
                    getAbsolutePos(new Vector2(getWidth(), getHeight()))
            };

            Vector2 outStart = getAbsolutePos(getCenter());
            Vector2 outEnd = outStart.copy();
            for (Vector2 pos : posList) {
                outStart.setX(Math.min(outStart.getX(), pos.getX()));
                outStart.setY(Math.min(outStart.getY(), pos.getY()));

                outEnd.setX(Math.max(outEnd.getX(), pos.getX()));
                outEnd.setY(Math.max(outEnd.getY(), pos.getY()));
            }

            return new Vector4(outStart, outEnd.subtract(outStart));
        }
        return copy();
    }

    public Matrix getAsMatix() {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(rotation, rotationPoint.getX(), rotationPoint.getY());
        matrix.postTranslate(pos.getX(), pos.getY());
        return matrix;
    }

    public Rect getAsRect() {
        return new Rect((int) getX(), (int) getY(), (int) (getX() + getWidth()), (int) (getY() + getHeight()));
    }

    public RectF getAsRectF() {
        return new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }
}
