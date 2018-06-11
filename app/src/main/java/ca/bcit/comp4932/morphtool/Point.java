package ca.bcit.comp4932.morphtool;

import java.io.Serializable;

/**
 * Created by Tyler on 2018-01-24.
 */

public class Point implements Serializable {
    public static int COLLISION_RADIUS = 40;
    private int x;
    private int y;
    private int displayX;
    private int displayY;
    private double d;
    private double weight;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.displayX = x;
        this.displayY = y;
    }

    public boolean contains(int posX, int posY) {
        return Math.pow(displayX - posX, 2) + Math.pow(displayY - posY, 2) <= Math.pow(COLLISION_RADIUS, 2);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDisplayPosition(int x, int y) {
        displayX = x;
        displayY = y;
    }

    public int getDisplayX() {
        return displayX;
    }

    public int getDisplayY() {
        return displayY;
    }

    public double getDistance() {
        return d;
    }

    public void setDistance(double distance) {
        d = distance;
    }

    public Point add(Point p) {
        return new Point(x + p.getX(), y + p.getY());
    }

    public Point minus(Point p) {
        return new Point(x - p.getX(), y - p.getY());
    }

    public Point scale(double num) {
        return new Point((int)(x * num), (int)(y * num));
    }

    public Point getPerpendicularVector() {
        return new Point(-y, x);
    }

    public double getVectorLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
