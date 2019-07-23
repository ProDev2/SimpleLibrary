package com.simplelib.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Line {
    private Vector2 start, end;

    public Line(Line src) {
        if (src == null)
            throw new NullPointerException();

        src.applyTo(this);
    }

    public Line(Vector2 start) {
        if (start == null)
            start = new Vector2();

        this.start = start;
        this.end = start.copy();
    }

    public Line(Vector2 start, Vector2 end) {
        if (start == null)
            start = new Vector2();
        if (end == null)
            end = new Vector2();

        if (start.equals(end))
            end = end.copy();

        this.start = start;
        this.end = end;
    }

    public Line(double x1, double y1, double x2, double y2) {
        this(new Vector2(x1, y1), new Vector2(x2, y2));
    }

    public Line setStart(double x, double y) {
        start.moveTo(x, y);
        return this;
    }

    public Line setEnd(double x, double y) {
        end.moveTo(x, y);
        return this;
    }

    public Line setStartX(double x) {
        start.setX(x);
        return this;
    }

    public Line setStartY(double y) {
        start.setY(y);
        return this;
    }

    public Line setEndX(double x) {
        end.setX(x);
        return this;
    }

    public Line setEndY(double y) {
        end.setY(y);
        return this;
    }

    public Vector2 getStart() {
        return start;
    }

    public Line setStart(Vector2 start) {
        if (start != null)
            this.start = start;
        return this;
    }

    public Vector2 getEnd() {
        return end;
    }

    public Line setEnd(Vector2 end) {
        if (end != null)
            this.end = end;
        return this;
    }

    public double getStartX() {
        return start.getX();
    }

    public double getStartY() {
        return start.getY();
    }

    public float getStartXAsFloat() {
        return start.getXAsFloat();
    }

    public float getStartYAsFloat() {
        return start.getYAsFloat();
    }

    public int getStartXAsInt() {
        return start.getXAsInt();
    }

    public int getStartYAsInt() {
        return start.getYAsInt();
    }

    public double getEndX() {
        return end.getX();
    }

    public double getEndY() {
        return end.getY();
    }

    public float getEndXAsFloat() {
        return end.getXAsFloat();
    }

    public float getEndYAsFloat() {
        return end.getYAsFloat();
    }

    public int getEndXAsInt() {
        return end.getXAsInt();
    }

    public int getEndYAsInt() {
        return end.getYAsInt();
    }

    public boolean isVertical() {
        return start.getX() == end.getX();
    }

    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    public Line moveBy(double dX, double dY) {
        start.moveBy(dX, dY);
        end.moveBy(dX, dY);
        return this;
    }

    public Line moveBy(Vector2 distance) {
        if (distance != null) {
            start.moveBy(distance);
            end.moveBy(distance);
        }
        return this;
    }

    public Line moveStart(double distance) {
        return moveStart(distance, true);
    }

    public Line moveStart(double distance, boolean noBounds) {
        Vector2 point = getPointFromStart(distance, noBounds);
        if (point != null)
            point.applyTo(start);
        return this;
    }

    public Line moveEnd(double distance) {
        return moveEnd(distance, true);
    }

    public Line moveEnd(double distance, boolean noBounds) {
        Vector2 point = getPointFromEnd(distance, noBounds);
        if (point != null)
            point.applyTo(end);
        return this;
    }

    public Line reverse() {
        Vector2 startTemp = start;
        Vector2 endTemp = end;

        start = endTemp;
        end = startTemp;

        return this;
    }

    public double getLength() {
        double dx = getStartX() - getEndX();
        double dy = getStartY() - getEndY();
        return (double) Math.sqrt((dx * dx) + (dy * dy));
    }

    public Line setLength(double length) {
        double resizeBy = length / getLength();

        end.subtract(start);
        end.multiply(new Vector2(resizeBy, resizeBy));
        end.add(start);
        return this;
    }

    public double getRelativeLength(double absoluteLength) {
        double length = getLength();
        if (length == 0d) return 0d;
        return absoluteLength / length;
    }

    public double getAbsoluteLength(double relativeLength) {
        double length = getLength();
        if (length == 0d) return 0d;
        return length * relativeLength;
    }

    public Line rotateBy(double angle) {
        PointRotator pointRotator = new PointRotator(end, start);
        pointRotator.rotate(angle).applyTo(end);
        return this;
    }

    public Line rotateTo(double angle) {
        double rotBy = angle - getAngle();
        if (rotBy != 0) rotateBy(rotBy);
        return this;
    }

    public double getAngle() {
        return (double) Math.toDegrees(Math.atan2(getEndY() - getStartY(), getEndX() - getStartX()));
    }

    public double getSmallestAngle(Line line) {
        double angle1 = getAngle();
        double angle2 = line.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        double d1 = angle2 - angle1;
        double d2 = angle1 + 360 - angle2;

        return Math.min(d1, d2);
    }

    public double getLargestAngle(Line line) {
        double angle1 = getAngle();
        double angle2 = line.getAngle();

        while (angle1 < angle2) angle1 += 360;
        while (angle1 > angle2) angle1 -= 360;

        double d1 = angle2 - angle1;
        double d2 = angle1 + 360 - angle2;

        return Math.max(d1, d2);
    }

    public Vector2 getCenter() {
        return new Vector2((getStartX() + getEndX()) / 2, (getStartY() + getEndY()) / 2);
    }

    public Vector2 getDelta() {
        return end.copy().subtract(start);
    }

    public double getDeltaX() {
        return getEndX() - getStartX();
    }

    public double getDeltaY() {
        return getEndY() - getStartY();
    }

    public float getDeltaXAsFloat() {
        return getEndXAsFloat() - getStartXAsFloat();
    }

    public float getDeltaYAsFloat() {
        return getEndYAsFloat() - getStartYAsFloat();
    }

    public int getDeltaXAsInt() {
        return getEndXAsInt() - getStartXAsInt();
    }

    public int getDeltaYAsInt() {
        return getEndYAsInt() - getStartYAsInt();
    }

    public Vector2 getPointFromStart(double distance) {
        return getPointFromStart(distance, true);
    }

    public Vector2 getPointFromStart(double distance, boolean noBounds) {
        double relativeLength = getRelativeLength(distance);
        return getPoint(relativeLength, noBounds);
    }

    public Vector2 getPointFromEnd(double distance) {
        return getPointFromEnd(distance, true);
    }

    public Vector2 getPointFromEnd(double distance, boolean noBounds) {
        double relativeLength = getRelativeLength(distance);
        return getPoint(1 - relativeLength, noBounds);
    }

    public Vector2 getPoint(double relativePos) {
        return getPoint(relativePos, true);
    }

    public Vector2 getPoint(double relativePos, boolean noBounds) {
        if (!noBounds) {
            if (relativePos < 0) relativePos = 0;
            if (relativePos > 1) relativePos = 1;
        }

        Vector2 delta = getDelta();
        if (delta.getX() == 0 && delta.getY() == 0) return null;

        double relPosX = delta.getX() * (double) relativePos;
        double relPosY = delta.getY() * (double) relativePos;
        return start.copy().add(new Vector2(relPosX, relPosY));
    }

    public double closestRelativePos(Vector2 pos) {
        return closestRelativePos(pos, false);
    }

    public double closestRelativePos(Vector2 pos, boolean noBounds) {
        Vector2 delta = getDelta();
        if (delta.getX() == 0 && delta.getY() == 0) return -1;

        double dX = (pos.getX() - getStartX()) * delta.getX();
        double dY = (pos.getY() - getStartY()) * delta.getY();
        double relPos = (dX + dY) / (Math.pow(delta.getX(), 2) + Math.pow(delta.getY(), 2));

        if (!noBounds) {
            if (relPos < 0) relPos = 0;
            if (relPos > 1) relPos = 1;
        }
        return (double) relPos;
    }

    public Line closestLineFrom(Vector2 pos) {
        return new Line(pos, closestPointTo(pos));
    }

    public Line closestLineTo(Vector2 pos) {
        return new Line(closestPointTo(pos), pos);
    }

    public Vector2 closestPointTo(Vector2 pos) {
        Vector2 delta = getDelta();
        if (delta.getX() == 0 && delta.getY() == 0) return null;

        double dX = (pos.getX() - getStartX()) * delta.getX();
        double dY = (pos.getY() - getStartY()) * delta.getY();
        double relPos = (dX + dY) / (Math.pow(delta.getX(), 2) + Math.pow(delta.getY(), 2));

        if (relPos < 0) {
            return start.copy();
        } else if (relPos > 1) {
            return end.copy();
        } else {
            double relPosX = delta.getX() * (double) relPos;
            double relPosY = delta.getY() * (double) relPos;
            return start.copy().add(new Vector2(relPosX, relPosY));
        }
    }

    public Vector2 intersectsAt(Line line) {
        if (!intersects(line)) return null;

        double x1line1 = getStartX();
        double y1line1 = getStartY();
        double x2line1 = getEndX();
        double y2line1 = getEndY();
        double x1line2 = line.getStartX();
        double y1line2 = line.getStartY();
        double x2line2 = line.getEndX();
        double y2line2 = line.getEndY();

        if ((x1line1 == x1line2) && (y1line1 == y1line2))
            return (new Vector2((double) x1line1, (double) y1line1));

        if ((x1line1 == x2line2) && (y1line1 == y2line2))
            return (new Vector2((double) x1line1, (double) y1line1));

        if ((x2line1 == x1line2) && (y2line1 == y1line2))
            return (new Vector2((double) x2line1, (double) y2line1));

        if ((x2line1 == x2line2) && (y2line1 == y2line2))
            return (new Vector2((double) x2line1, (double) y2line1));

        double dyline1 = -(y2line1 - y1line1);
        double dxline1 = x2line1 - x1line1;
        double dyline2 = -(y2line2 - y1line2);
        double dxline2 = x2line2 - x1line2;

        double e = -(dyline1 * x1line1) - (dxline1 * y1line1);
        double f = -(dyline2 * x1line2) - (dxline2 * y1line2);

        if ((dyline1 * dxline2 - dyline2 * dxline1) == 0)
            return null;

        Vector2 point = new Vector2();
        point.setX((double) (-(e * dxline2 - dxline1 * f) / (dyline1 * dxline2 - dyline2 * dxline1)));
        point.setY((double) (-(dyline1 * f - dyline2 * e) / (dyline1 * dxline2 - dyline2 * dxline1)));

        return point;
    }

    public boolean intersects(Line line) {
        Vector2 start1 = start;
        Vector2 end1 = end;
        Vector2 start2 = line.start;
        Vector2 end2 = line.end;

        double A1 = end1.y - start1.y;
        double B1 = start1.x - end1.x;
        double C1 = A1 * start1.x + B1 * start1.y;

        double A2 = end2.y - start2.y;
        double B2 = start2.x - end2.x;
        double C2 = A2 * start2.x + B2 * start2.y;

        double det = (A1 * B2) - (A2 * B1);

        if (det == 0) {
            if ((A1 * start2.x) + (B1 * start2.y) == C1) {
                if ((Math.min(start1.x, end1.x) < start2.x) && (Math.max(start1.x, end1.x) > start2.x))
                    return true;

                if ((Math.min(start1.x, end1.x) < end2.x) && (Math.max(start1.x, end1.x) > end2.x))
                    return true;

                return false;
            }
            return false;
        } else {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;

            if ((x >= Math.min(start1.x, end1.x) && x <= Math.max(start1.x, end1.x)) && (y >= Math.min(start1.y, end1.y) && y <= Math.max(start1.y, end1.y))) {
                if ((x >= Math.min(start2.x, end2.x) && x <= Math.max(start2.x, end2.x)) && (y >= Math.min(start2.y, end2.y) && y <= Math.max(start2.y, end2.y)))
                    return true;
            }
            return false;
        }
    }

    public List<Intersection> intersects(Line... lines) {
        List<Line> lineList = new ArrayList<>();
        lineList.addAll(Arrays.asList(lines));
        return intersects(lineList);
    }

    public List<Intersection> intersects(List<Line> lines) {
        List<Intersection> intersectionList = new ArrayList<>();
        for (Line line : lines) {
            if (!equals(line)) {
                Vector2 pos = intersectsAt(line);
                if (pos != null)
                    intersectionList.add(new Intersection(pos, line, getStart().copy()));
            }
        }
        if (intersectionList.size() > 0) {
            Collections.sort(intersectionList, new Comparator<Intersection>() {
                @Override
                public int compare(Intersection i1, Intersection i2) {
                    if (i1.getRayToIntersection().getLength() < i2.getRayToIntersection().getLength())
                        return -1;
                    else if (i1.getRayToIntersection().getLength() > i2.getRayToIntersection().getLength())
                        return 1;

                    return 0;
                }
            });
        }
        return intersectionList;
    }

    public Intersection intersectsFirst(Line... lines) {
        List<Line> lineList = new ArrayList<>();
        lineList.addAll(Arrays.asList(lines));
        return intersectsFirst(lineList);
    }

    public Intersection intersectsFirst(List<Line> lines) {
        List<Intersection> intersectionList = intersects(lines);
        if (intersectionList.size() > 0)
            return intersectionList.get(0);
        else
            return null;
    }

    public boolean isEqualTo(Line src) {
        if (src == null) return false;
        return start.isEqualTo(src.start) && end.isEqualTo(src.end);
    }

    public void applyTo(Line line) {
        line.start = start.copy();
        line.end = end.copy();
    }

    public Line copy() {
        return new Line(this);
    }

    public static class Intersection {
        private Vector2 pos;
        private Line line;

        private Vector2 rayStartPos;
        private Line rayToIntersection;

        public Intersection(Vector2 pos, Line line, Vector2 rayStartPos) {
            this.pos = pos;
            this.line = line;

            this.rayStartPos = rayStartPos;
            this.rayToIntersection = new Line(rayStartPos, pos);
        }

        public Vector2 getPos() {
            return pos;
        }

        public Line getLine() {
            return line;
        }

        public Vector2 getStartPos() {
            return rayStartPos;
        }

        public Line getRayToIntersection() {
            return rayToIntersection;
        }
    }
}