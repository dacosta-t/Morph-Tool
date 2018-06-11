package ca.bcit.comp4932.morphtool;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Tyler on 2018-01-29.
 */

public class Frame implements Serializable {
    private final int [] pixels;
    private final int width , height;

    public Frame(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int [width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public Bitmap getBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}