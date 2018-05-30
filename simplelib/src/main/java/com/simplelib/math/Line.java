package com.simplelib.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
        this.start = start;
        this.end = end;

        if (start == null)
            start = new Vector2();
        if (end == null)
            end = new Vector2();

        if (start.equals(end))
            end = end.copy();
    }

    public Line(float x1, float y1, float x2, float y2) {
        this(new Vector2(x1, y1), new Vector2(x2, y2));
    }

    public Line setStart(float x, float y) {
        start.moveTo(x, y);
        return this;
    }

    public Line setEnd(float x, float y) {
        end.moveTo(x, y);
        return this;
    }

    public Line setStartX(float x) {
        start.setX(x);
        return this;
    }

    public Line setStartY(float y) {
        start.setY(y);
        return this;
    }

    public Line setEndX(float x) {
        end.setX(x);
        return this;
    }

    public Line setEndY(float y) {
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

    public float getStartX() {
        return start.getX();
    }

    public float getStartY() {
        return start.getY();
    }

    public float getEndX() {
        return end.getX();
    }

    public float getEndY() {
        return end.getY();
    }

    public boolean isVertical() {
        return start.getX() == end.getX();
    }

    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    public Line reverse() {
        Vector2 startTemp = start;
        Vector2 endTemp = end;

        start = endTemp;
        end = startTemp;

        return this;
    }

    public float getLength() {
        double dx = getStartX() - getEndX();
        double dy = getStartY() - getEndY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float getAngle() {
        return (float) Math.toDegrees(Math.atan2(getEndY() - getStartY(), getEndX() - getStartX()));
    }

    public Vector2 getCenter() {
        return new Vector2((getStartX() + getEndX()) / 2, (getStartY() + getEndY()) / 2);
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
            return (new Vector2((float) x1line1, (float) y1line1));

        if ((x1line1 == x2line2) && (y1line1 == y2line2))
            return (new Vector2((float) x1line1, (float) y1line1));

        if ((x2line1 == x1line2) && (y2line1 == y1line2))
            return (new Vector2((float) x2line1, (float) y2line1));

        if ((x2line1 == x2line2) && (y2line1 == y2line2))
            return (new Vector2((float) x2line1, (float) y2line1));

        double dyline1 = -(y2line1 - y1line1);
        double dxline1 = x2line1 - x1line1;
        double dyline2 = -(y2line2 - y1line2);
        double dxline2 = x2line2 - x1line2;

        double e = -(dyline1 * x1line1) - (dxline1 * y1line1);
        double f = -(dyline2 * x1line2) - (dxline2 * y1line2);

        if ((dyline1 * dxline2 - dyline2 * dxline1) == 0)
            return null;

        Vector2 point = new Vector2();
        point.setX((float) (-(e * dxline2 - dxline1 * f) / (dyline1 * dxline2 - dyline2 * dxline1)));
        point.setY((float) (-(dyline1 * f - dyline2 * e) / (dyline1 * dxline2 - dyline2 * dxline1)));

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

    public ArrayList<Intersection> intersects(Line... lines) {
        ArrayList<Line> lineList = new ArrayList<>();
        lineList.addAll(Arrays.asList(lines));
        return intersects(lineList);
    }

    public ArrayList<Intersection> intersects(ArrayList<Line> lines) {
        ArrayList<Intersection> intersectionList = new ArrayList<>();
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
        ArrayList<Line> lineList = new ArrayList<>();
        lineList.addAll(Arrays.asList(lines));
        return intersectsFirst(lineList);
    }

    public Intersection intersectsFirst(ArrayList<Line> lines) {
        ArrayList<Intersection> intersectionList = intersects(lines);
        if (intersectionList.size() > 0)
            return intersectionList.get(0);
        else
            return null;
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