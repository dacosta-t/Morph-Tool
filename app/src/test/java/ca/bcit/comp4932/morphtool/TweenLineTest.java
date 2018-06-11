package ca.bcit.comp4932.morphtool;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tyler on 2018-01-25.
 */

public class TweenLineTest {

    @Test
    public void getTweenLineTest() {
        Point a0 = new Point(20, 20);
        Point a1 = new Point(20, 0);
        Point b0 = new Point(0, 10);
        Point b1 = new Point(30, 10);

        Line a0a1 = new Line(a0, a1);
        Line b0b1 = new Line(b0, b1);

        LinePair lp = new LinePair(a0a1, b0b1);
        Morpher morph = new Morpher();
        Line tl = morph.getTweenLine(lp, 0.5);

        assertTrue("Tween line incorrect", tl.getHead().getX() == 25);
    }
}
