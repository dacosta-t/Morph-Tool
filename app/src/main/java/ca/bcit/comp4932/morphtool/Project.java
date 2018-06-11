package ca.bcit.comp4932.morphtool;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tyler on 2018-01-24.
 */

public class Project implements Serializable {
    private List<LinePair> lines;
    private LinkedList<Frame> morph;
    private Frame leftImage;
    private Frame rightImage;

    public Project() {
        lines = new ArrayList<>();
        morph = new LinkedList<>();
    }

    public List<LinePair> getLines() {
        return lines;
    }

    public LinkedList<Frame> getMorph() {
        return morph;
    }

    public void setMorph(LinkedList<Frame> frames) {
        morph = frames;
    }

    public Bitmap getLeftImage() {
        if (leftImage != null) {
            return leftImage.getBitmap();
        } else {
            return null;
        }
    }

    public Bitmap getRightImage() {
        if (rightImage != null) {
            return rightImage.getBitmap();
        } else {
            return null;
        }
    }

    public void setLeftImage(Bitmap bitmap) {
        morph.remove(leftImage);
        leftImage = new Frame(bitmap);
        morph.add(leftImage);
    }

    public void setRightImage(Bitmap bitmap) {
        morph.remove(rightImage);
        rightImage = new Frame(bitmap);
        morph.add(rightImage);
    }

    public List<LinePair> reverseLines() {
        List<LinePair> reverse = new ArrayList<>();
        for (int i = lines.size() - 1; i >= 0; i--) {
            LinePair linePair = new LinePair(lines.get(i).getRightLine(), lines.get(i).getLeftLine());
            reverse.add(linePair);
        }

        return reverse;
    }

    public void saveProject(Context context, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (IOException e) {}
    }
}
