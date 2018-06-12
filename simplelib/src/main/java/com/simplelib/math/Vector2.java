package com.simplelib.math;

public class Vector2 {
    public float x, y;

    public Vector2() {
    }

    public Vector2(Vector2 src) {
        src.applyTo(this);
    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getLength() {
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public Vector2 setLength(float length) {
        float resizeBy = length / getLength();
        multiply(resizeBy);
        return this;
    }

    public Vector2 rotateBy(float angle) {
        PointRotator pointRotator = new PointRotator(this);
        pointRotator.rotate(angle).applyTo(this);
        return this;
    }

    public Vector2 rotateTo(float angle) {
        float rotBy = angle - getAngle();
        if (rotBy != 0) rotateBy(rotBy);
        return this;
    }

    public float getAngle() {
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    public float getSmallestAngle(Vector2 vector) {
        float angle1 = getAngle();
        float angle2 = vector.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        float d1 = angle2 - angle1;
        float d2 = angle1 + 360 - angle2;

        return Math.min(d1, d2);
    }

    public float getLargestAngle(Vector2 vector) {
        float angle1 = getAngle();
        float angle2 = vector.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        float d1 = angle2 - angle1;
        float d2 = angle1 + 360 - angle2;

        return Math.max(d1, d2);
    }

    public float getX() {
        return x;
    }

    public Vector2 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector2 setX(int x) {
        this.x = x;
        return this;
    }

    public int getXAsInt() {
        return (int) x;
    }

    public float getY() {
        return y;
    }

    public Vector2 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector2 setY(int y) {
        this.y = y;
        return this;
    }

    public int getYAsInt() {
        return (int) y;
    }

    public Vector2 add(Vector2 vector) {
        if (vector != null) {
            this.x += vector.getX();
            this.y += vector.getY();
        }
        return this;
    }

    public Vector2 subtract(Vector2 vector) {
        if (vector != null) {
            this.x -= vector.getX();
            this.y -= vector.getY();
        }
        return this;
    }

    public Vector2 multiply(Vector2 vector) {
        if (vector != null) {
            this.x *= vector.getX();
            this.y *= vector.getY();
        }
        return this;
    }

    public Vector2 divide(Vector2 vector) {
        if (vector != null) {
            this.x /= vector.getX();
            this.y /= vector.getY();
        }
        return this;
    }

    public Vector2 add(float value) {
        this.x += value;
        this.y += value;
        return this;
    }

    public Vector2 subtract(float value) {
        this.x -= value;
        this.y -= value;
        return this;
    }

    public Vector2 multiply(float value) {
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Vector2 divide(float value) {
        this.x /= value;
        this.y /= value;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 multiply(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 divide(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2 negate() {
        this.x = -x;
        this.y = -y;
        return this;
    }

    public Vector2 toMinSize() {
        float size = Math.min(x, y);
        this.x = size;
        this.y = size;
        return this;
    }

    public Vector2 toMaxSize() {
        float size = Math.max(x, y);
        this.x = size;
        this.y = size;
        return this;
    }

    public Vector2 moveTo(Vector2 pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        return this;
    }

    public Vector2 moveBy(Vector2 distance) {
        this.x += distance.getX();
        this.y += distance.getY();
        return this;
    }

    public Vector2 moveTo(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 moveBy(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 moveBy(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 applyTo(Vector2 vector) {
        if (vector != null) {
            vector.x = x;
            vector.y = y;
        }
        return this;
    }

    public boolean isNull() {
        return x == 0 && y == 0;
    }

    public Vector2 copy() {
        return new Vector2(this);
    }
}
