package ca.bcit.comp4932.morphtool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Tyler on 2018-01-24.
 */

public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private MainActivity activity;
    private DrawFragment fragment;
    private List<LinePair> lines;
    private Bitmap image;
    private Paint drawPaint;
    private DrawThread drawThread;
    private Point selectedPoint;
    private boolean isLeftView;

    private int originalWidth;
    private int originalHeight;
    private int scaleWidth;
    private int scaleHeight;

    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        activity = (MainActivity) context;
        fragment = (DrawFragment) activity.getFragmentManager().findFragmentById(R.id.content_frame);
        drawPaint = new Paint();
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setStrokeWidth(Line.LINE_WIDTH);
        drawPaint.setColor(Color.RED);
        lines = ((MainActivity)getContext()).getProject().getLines();

        isLeftView = getId() == R.id.leftImage;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawThread = new DrawThread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        drawThread.stopThread();
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            super.draw(canvas);

            if (image != null) {
                canvas.drawBitmap(image, 0, 0, null);
            }

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).isSelected()) {
                    drawPaint.setColor(Color.GREEN);
                } else {
                    drawPaint.setColor(Color.RED);
                }
                Line line = isLeftView ? lines.get(i).getLeftLine() : lines.get(i).getRightLine();
                line.draw(canvas, drawPaint, fragment.getMode());
            }
        }
    }

    private void onTouchDown(int x, int y) {
        if (fragment.getMode() == DrawFragment.Modes.DRAW) {
            LinePair linePair = new LinePair(x, y);
            if (scaleWidth != 0 && scaleHeight != 0) {
                linePair.setTailPoints(x * originalWidth / scaleWidth, y * originalHeight / scaleHeight);
            }
            lines.add(linePair);
        } else if (fragment.getMode() == DrawFragment.Modes.EDIT) {
            for (int i = 0; i < lines.size(); i++) {
                lines.get(i).setSelected(false);
            }
            selectedPoint = null;
            for (int i = 0; i < lines.size(); i++) {
                LinePair linePair = lines.get(i);
                Line line = isLeftView ? linePair.getLeftLine() : linePair.getRightLine();
                selectedPoint = line.getTouchedPoint(x, y);
                if (selectedPoint != null) {
                    linePair.setSelected(true);
                    break;
                }
            }
        }
    }

    private void onTouchMove(int x, int y) {
        if (fragment.getMode() == DrawFragment.Modes.DRAW) {
            LinePair linePair = lines.get(lines.size() - 1);
            linePair.setHeadDisplayPoints(x, y);
            if (scaleWidth != 0 && scaleHeight != 0) {
                linePair.setHeadPoints(x * originalWidth / scaleWidth, y * originalHeight / scaleHeight);
            } else {
                linePair.setHeadPoints(x, y);
            }
        } else if (fragment.getMode() == DrawFragment.Modes.EDIT && selectedPoint != null) {
            selectedPoint.setDisplayPosition(x, y);
            if (scaleWidth != 0 && scaleHeight != 0) {
                selectedPoint.setPosition(x * originalWidth / scaleWidth, y * originalHeight / scaleHeight);
            } else {
                selectedPoint.setPosition(x, y);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        if (x < 0) x = 0;
        if (x >= getWidth()) x = getWidth();
        if (y < 0) y = 0;
        if (y >= getHeight()) y = getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                break;
        }
        return true;
    }

    public void setBitmap(Bitmap bmp) {
        originalWidth = bmp.getWidth();
        originalHeight = bmp.getHeight();
        int maxWidth = activity.getMaxViewWidth();
        int maxHeight = activity.getMaxViewHeight();
        float ratio = originalWidth / (float) originalHeight;
        int viewWidth = (int) (maxHeight * ratio);
        if (viewWidth > maxWidth) {
            scaleHeight = (int)(maxWidth * originalHeight / (float) originalWidth);
            scaleWidth = maxWidth;
        } else {
            scaleWidth = viewWidth;
            scaleHeight = maxHeight;
        }

        image = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);

        setMargins();
    }

    public void setMargins() {
        if (image != null) {
            int width = (activity.getMaxViewWidth() - scaleWidth) / 2;
            int height = (activity.getMaxViewHeight() - scaleHeight) / 2;
            ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) getLayoutParams();
            margin.setMargins(width, height, width, height);
            requestLayout();
        }
    }
}
