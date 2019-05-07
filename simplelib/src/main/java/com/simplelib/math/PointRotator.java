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