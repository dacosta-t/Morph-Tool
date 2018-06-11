package ca.bcit.comp4932.morphtool;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Tyler on 2018-01-24.
 */

public class DrawFragment extends Fragment {
    enum Modes {
        DRAW,
        EDIT
    }

    private MainActivity activity;
    private Modes currMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        currMode = Modes.DRAW;
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.draw_mode_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        Project project = activity.getProject();
        Bitmap image = project.getLeftImage();
        if (image != null) {
            DrawSurfaceView drawView = (DrawSurfaceView) activity.findViewById(R.id.leftImage);
            drawView.setBitmap(image);
        }

        image = project.getRightImage();
        if (image != null) {
            DrawSurfaceView drawView = (DrawSurfaceView) activity.findViewById(R.id.rightImage);
            drawView.setBitmap(image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        List<LinePair> lines;
        switch (id) {
            case R.id.draw_mode:
                currMode = Modes.DRAW;
                lines = activity.getProject().getLines();
                for (int i = 0; i < lines.size(); i++) {
                    lines.get(i).setSelected(false);
                }
                return true;
            case R.id.edit_mode:
                currMode = Modes.EDIT;
                return true;
            case R.id.delete:
                lines = activity.getProject().getLines();
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).isSelected()) {
                        lines.remove(i);
                        break;
                    }
                }
                return true;
            case R.id.delete_all:
                if (currMode == Modes.EDIT) {
                    activity.getProject().getLines().clear();
                    currMode = Modes.DRAW;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Modes getMode() {
        return currMode;
    }
}