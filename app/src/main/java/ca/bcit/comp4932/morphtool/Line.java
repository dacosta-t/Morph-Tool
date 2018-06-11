package ca.bcit.comp4932.morphtool;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by Tyler on 2018-01-24.
 */

public class Line implements Serializable {
    public static int LINE_WIDTH = 5;

    private Point head;
    private Point tail;

    public Line(int x, int y) {
        tail = new Point(x, y);
        head = new Point(x, y);
    }

    public Line(Point tail, Point head) {
        this.tail = tail;
        this.head = head;
    }

    public void draw(Canvas canvas, Paint drawPaint, DrawFragment.Modes mode) {
        canvas.drawLine(tail.getDisplayX(), tail.getDisplayY(), head.getDisplayX(), head.getDisplayY(), drawPaint);
        if (mode == DrawFragment.Modes.EDIT) {
            canvas.drawCircle(tail.getDisplayX(), tail.getDisplayY(), Point.COLLISION_RADIUS, drawPaint);
            canvas.drawCircle(head.getDisplayX(), head.getDisplayY(), Point.COLLISION_RADIUS, drawPaint);
        }
    }

    public Point getTail() {
        return tail;
    }

    public Point getHead() {
        return head;
    }

    public void setHead(int x, int y) {
        head.setPosition(x, y);
    }

    public void setTail(int x, int y) {
        tail.setPosition(x, y);
    }

    public Point getTouchedPoint(int x, int y) {
        if (tail.contains(x, y)) {
            return tail;
        } else if (head.contains(x, y)) {
            return head;
        }

        return null;
    }

    public Point getVector() {
        int x = head.getX() - tail.getX();
        int y = head.getY() - tail.getY();
        return new Point(x, y);
    }
}
