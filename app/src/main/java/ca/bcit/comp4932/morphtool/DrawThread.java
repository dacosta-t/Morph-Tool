package ca.bcit.comp4932.morphtool;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Tyler on 2018-01-24.
 */

public class DrawThread extends Thread {
    private boolean isRunning;
    private DrawSurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    public DrawThread(DrawSurfaceView view) {
        surfaceView = view;
        surfaceHolder = view.getHolder();
    }

    @Override
    public void run() {
        isRunning = true;
        Canvas c = null;

        while (isRunning) {
            try {
                c = surfaceHolder.lockCanvas();
                surfaceView.draw(c);
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    public void stopThread() {
        isRunning = false;
    }
}
