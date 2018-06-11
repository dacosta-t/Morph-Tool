package ca.bcit.comp4932.morphtool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tyler on 2018-01-24.
 */

public class Morpher {
    private List<LinePair> linePairs;
    private int numFrames;

    public Morpher() {

    }

    public Morpher(List<LinePair> linePairs, int numFrames) {
        this.linePairs = linePairs;
        this.numFrames = numFrames;
    }

    public LinkedList<Frame> morph(Bitmap src, Bitmap dest, boolean forward) {
        LinkedList<Frame> frames = new LinkedList<>();
        LinkedList<ArrayList<Line>> lines = generateLines();
        if (forward) {
            frames.add(new Frame(src));
        } else {
            frames.add(new Frame(dest));
        }
        for (int i = 0; i < numFrames; i++) {
            android.util.Log.d("Frame: ", "" + i);
            Bitmap frame = Bitmap.createBitmap(dest.getWidth(), dest.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(frame);
            for (int y = 0; y < frame.getHeight(); y++) {
                for (int x = 0; x < frame.getWidth(); x++) {
                    Point p = new Point(x, y);
                    ArrayList<Line> srcLines = lines.get(i + 1);
                    ArrayList<Line> destLines = lines.get(i);

                    double totalWeight = 0;
                    double totalDeltaX = 0;
                    double totalDeltaY = 0;

                    for (int line = 0; line < destLines.size(); line++) {
                        Point pixel = getSrcFromPixel(p, srcLines.get(line), destLines.get(line));

                        double weight = Math.pow(1 / (0.01 + pixel.getDistance()), 2);
                        totalWeight += weight;
                        totalDeltaX += (pixel.getX() - p.getX()) * weight;
                        totalDeltaY += (pixel.getY() - p.getY()) * weight;
                    }

                    int pX = (int)((totalDeltaX / totalWeight) + p.getX());
                    int pY = (int)((totalDeltaY / totalWeight) + p.getY());

                    if (pX < 0) {
                        pX = 0;
                    } else if (pX >= src.getWidth()) {
                        pX = src.getWidth() - 1;
                    }
                    if (pY < 0) {
                        pY = 0;
                    } else if (pY >= src.getHeight()) {
                        pY = src.getHeight() - 1;
                    }

                    int colour = src.getPixel(pX, pY);
                    frame.setPixel(x, y, colour);
                }
            }
            canvas.drawBitmap(frame, 0, 0, null);
            frames.add(new Frame(frame));
        }

        if (forward) {
            frames.add(new Frame(dest));
        } else {
            frames.add(new Frame(src));
        }
        return frames;
    }

    public Point getSrcFromPixel(Point t, Line start, Line end) {
        Point pq = start.getVector();
        Point normal = pq.getPerpendicularVector();
        Point tp = new Line(t, start.getTail()).getVector();
        Point pt = new Line(start.getTail(), t).getVector();
        double d = projectVector(tp, normal);
        double fractionalPercent = projectVector(pt, pq) / pq.getVectorLength();
        Point pqPrime = end.getVector();
        Point normalPrime = pqPrime.getPerpendicularVector();
        int x = (int)(end.getTail().getX() + fractionalPercent * pqPrime.getX() - d * (normalPrime.getX() / normalPrime.getVectorLength()));
        int y = (int)(end.getTail().getY() + fractionalPercent * pqPrime.getY() - d * (normalPrime.getY() / normalPrime.getVectorLength()));
        Point p = new Point(x, y);
        p.setDistance(d);
        return p;
    }

    private double projectVector(Point a, Point b) {
        return ((a.getX() * b.getX()) + (a.getY() * b.getY())) / b.getVectorLength();
    }

    private LinkedList<ArrayList<Line>> generateLines() {
        LinkedList<ArrayList<Line>> lines = new LinkedList<>();
        ArrayList<Line> startLines = new ArrayList<>();
        ArrayList<Line> endLines = new ArrayList<>();

        for (int i = 0; i < numFrames; i++) {
            lines.add(new ArrayList<Line>());
        }

        for (int i = 0; i < linePairs.size(); i++) {
            startLines.add(linePairs.get(i).getLeftLine());
            endLines.add(linePairs.get(i).getRightLine());

            double morphFraction = 1.0 / (numFrames + 1);

            for (int frame = 1; frame <= numFrames; frame++) {
                lines.get(frame - 1).add(getTweenLine(linePairs.get(i), frame * morphFraction));
            }
        }
        lines.addFirst(startLines);
        lines.addLast(endLines);

        return lines;
    }

    public Line getTweenLine(LinePair linePair, double fraction) {
        Line start = linePair.getLeftLine();
        Line end = linePair.getRightLine();

        // p0 + (p1 - p0) * fraction
        Point tail = start.getTail().add((end.getTail().minus(start.getTail())).scale(fraction));
        Point head = start.getHead().add((end.getHead().minus(start.getHead())).scale(fraction));

        return new Line(tail, head);
    }

    public LinkedList<Frame> crossDissolve(LinkedList<Frame> left, LinkedList<Frame> right) {
        LinkedList<Frame> frames = new LinkedList<>();
        frames.add(left.getFirst());
        int frameCount = left.size() - 1;
        for (int frame = 1; frame < left.size() - 1; frame++) {
            Bitmap leftImage = left.get(frame).getBitmap();
            Bitmap rightImage = right.get(frame).getBitmap();
            Bitmap newFrame = Bitmap.createBitmap(leftImage.getWidth(), leftImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newFrame);
            for (int y = 0; y < leftImage.getHeight(); y++) {
                for (int x = 0; x < leftImage.getWidth(); x++) {
                    int leftColour = leftImage.getPixel(x, y);
                    int rightColour = rightImage.getPixel(x, y);

                    int red = (int)(Color.red(leftColour) * (1 - frame / (float)frameCount) + Color.red(rightColour) * (frame / (float)frameCount));
                    int green = (int)(Color.green(leftColour) * (1 - frame / (float)frameCount) + Color.green(rightColour) * (frame / (float)frameCount));
                    int blue = (int)(Color.blue(leftColour) * (1 - frame / (float)frameCount) + Color.blue(rightColour) * (frame / (float)frameCount));
                    int alpha = (int)(Color.alpha(leftColour) * (1 - frame / (float)frameCount) + Color.alpha(rightColour) * (frame / (float)frameCount));

                    newFrame.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
            }
            canvas.drawBitmap(newFrame, 0, 0, null);
            frames.add(new Frame(newFrame));
        }
        frames.add(right.getLast());
        return frames;
    }
}