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

public class PointRotator {
    private Vector2 point;
    private Vector2 center;

    public PointRotator(Vector2 point) {
        this(point, new Vector2());
    }

    public PointRotator(Vector2 point, Vector2 center) {
        this.point = point;
        this.center = center;
    }

    public void movePointTo(int x, int y) {
        this.point.moveTo(x, y);
    }

    public void moveCenterTo(int x, int y) {
        this.center.moveTo(x, y);
    }

    public void movePointBy(int x, int y) {
        this.point.moveBy(x, y);
    }

    public void moveCenterBy(int x, int y) {
        this.center.moveBy(x, y);
    }

    public Vector2 rotate(double angle) {
        double rad = Math.toRadians(angle);

        double xStart = point.x - center.x;
        double yStart = point.y - center.y;
        double x = (xStart * Math.cos(rad)) - (yStart * Math.sin(rad));
        double y = (xStart * Math.sin(rad)) + (yStart * Math.cos(rad));
        x += center.x;
        y += center.y;

        return new Vector2((double) x, (double) y);
    }

    public double getAngleTo(Vector2 point2) {
        double angleOfPoint = Math.atan2(point.getY() - center.getY(), point.getX() - center.getX());
        double angleOfPoint2 = Math.atan2(point2.getY() - center.getY(), point2.getX() - center.getX());

        return Math.toDegrees(angleOfPoint2 - angleOfPoint);
    }
}