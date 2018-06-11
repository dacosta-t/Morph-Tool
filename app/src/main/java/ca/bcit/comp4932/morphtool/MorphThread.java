package ca.bcit.comp4932.morphtool;


import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tyler on 2018-01-29.
 */

public class MorphThread extends Thread {

    private MainActivity activity;
    private Bitmap src;
    private Bitmap dest;
    private List<LinePair> linePairs;
    private int numFrames;
    private LinkedList<Frame> frames;
    private boolean isForward;

    public MorphThread(MainActivity activity, Bitmap src, Bitmap dest, List<LinePair> linePairs, int numFrames, boolean forward) {
        this.src = src;
        this.dest = dest;
        this.linePairs = linePairs;
        this.numFrames = numFrames;
        isForward = forward;
    }

    @Override
    public void run() {
        Morpher morph = new Morpher(linePairs, numFrames);
        frames = morph.morph(src, dest, isForward);
    }

    public LinkedList<Frame> getMorph() {
        return frames;
    }
}
