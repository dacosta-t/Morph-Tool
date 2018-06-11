package ca.bcit.comp4932.morphtool;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tyler on 2018-01-25.
 */

public class GetSourcePixelTest {
    @Test
    public void getSrcPixelFromPixel() {
        Morpher morph = new Morpher();
        Point p = new Point(100, 300);
        Point q = new Point(300, 100);
        Point pPrm = new Point(100, 200);
        Point qPrm = new Point(300, 150);
        Point x = new Point(0, 0);
        Line srcLine = new Line(p, q);
        Line destLine = new Line(pPrm, qPrm);

        Point xPrm = morph.getSrcFromPixel(x, srcLine, destLine);

        assertTrue("Source pixel is: " + xPrm.getX(), xPrm.getX() == 131);
    }
}
