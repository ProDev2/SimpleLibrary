/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.math;

public class Vector2 {
    public double x, y;

    public Vector2() {
    }

    public Vector2(Vector2 src) {
        src.applyTo(this);
    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return (double) Math.sqrt((x * x) + (y * y));
    }

    public Vector2 setLength(double length) {
        double resizeBy = length / getLength();
        multiply(resizeBy);
        return this;
    }

    public Vector2 rotateBy(double angle) {
        PointRotator pointRotator = new PointRotator(this);
        pointRotator.rotate(angle).applyTo(this);
        return this;
    }

    public Vector2 rotateTo(double angle) {
        double rotBy = angle - getAngle();
        if (rotBy != 0) rotateBy(rotBy);
        return this;
    }

    public double getAngle() {
        return (double) Math.toDegrees(Math.atan2(y, x));
    }

    public double getSmallestAngle(Vector2 vector) {
        double angle1 = getAngle();
        double angle2 = vector.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        double d1 = angle2 - angle1;
        double d2 = angle1 + 360 - angle2;

        return Math.min(d1, d2);
    }

    public double getLargestAngle(Vector2 vector) {
        double angle1 = getAngle();
        double angle2 = vector.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        double d1 = angle2 - angle1;
        double d2 = angle1 + 360 - angle2;

        return Math.max(d1, d2);
    }

    public Vector2 set(Vector2 vector) {
        if (vector != null)
            vector.applyTo(this);
        return this;
    }

    public Vector2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public double getX() {
        return x;
    }

    public float getXAsFloat() {
        return (float) x;
    }

    public int getXAsInt() {
        return (int) x;
    }

    public Vector2 setX(double x) {
        this.x = x;
        return this;
    }

    public Vector2 setX(int x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public float getYAsFloat() {
        return (float) y;
    }

    public int getYAsInt() {
        return (int) y;
    }

    public Vector2 setY(double y) {
        this.y = y;
        return this;
    }

    public Vector2 setY(int y) {
        this.y = y;
        return this;
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

    public Vector2 add(double value) {
        this.x += value;
        this.y += value;
        return this;
    }

    public Vector2 subtract(double value) {
        this.x -= value;
        this.y -= value;
        return this;
    }

    public Vector2 multiply(double value) {
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Vector2 divide(double value) {
        this.x /= value;
        this.y /= value;
        return this;
    }

    public Vector2 add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 multiply(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 divide(double x, double y) {
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
        double size = Math.min(x, y);
        this.x = size;
        this.y = size;
        return this;
    }

    public Vector2 toMaxSize() {
        double size = Math.max(x, y);
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

    public Vector2 moveTo(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 moveBy(double x, double y) {
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

    public boolean isNull() {
        return x == 0 && y == 0;
    }

    public boolean isEqualTo(Vector2 src) {
        if (src == null) return false;
        return x == src.x && y == src.y;
    }

    public Vector2 applyTo(Vector2 vector) {
        if (vector != null) {
            vector.x = x;
            vector.y = y;
        }
        return this;
    }

    public Vector2 copy() {
        return new Vector2(this);
    }
}
