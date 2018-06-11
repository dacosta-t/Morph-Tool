package ca.bcit.comp4932.morphtool;

import java.io.Serializable;

/**
 * Created by Tyler on 2018-01-24.
 */

public class LinePair implements Serializable {
    private Line leftLine;
    private Line rightLine;
    private boolean isSelected;

    public LinePair(int x, int y) {
        leftLine = new Line(x, y);
        rightLine = new Line(x, y);
        isSelected = false;
    }

    public LinePair(Line leftLine, Line rightLine) {
        this.leftLine = leftLine;
        this.rightLine = rightLine;
        isSelected = false;
    }

    public Line getLeftLine() {
        return leftLine;
    }

    public Line getRightLine() {
        return rightLine;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setHeadPoints(int x, int y) {
        leftLine.setHead(x, y);
        rightLine.setHead(x, y);
    }

    public void setTailPoints(int x, int y) {
        leftLine.setTail(x, y);
        rightLine.setTail(x, y);
    }

    public void setTailDisplayPoints(int x, int y) {
        leftLine.getHead().setDisplayPosition(x, y);
        rightLine.getHead().setDisplayPosition(x, y);
    }

    public void setHeadDisplayPoints(int x, int y) {
        leftLine.getHead().setDisplayPosition(x, y);
        rightLine.getHead().setDisplayPosition(x, y);
    }
}
