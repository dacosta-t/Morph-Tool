package ca.bcit.comp4932.morphtool;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Tyler on 2018-01-24.
 */

public class MorphViewFragment extends Fragment {

    private MainActivity activity;
    private Project project;
    private Menu menu;
    private int index;
    private boolean isPlayingForward;
    private boolean isPlayingReverse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_morph_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        project = activity.getProject();
        int frameCount = project.getMorph().size();
        isPlayingForward = false;
        isPlayingReverse = false;
        index = 0;
        if (project.getMorph().size() > 0) {
            ImageView view = (ImageView) activity.findViewById(R.id.morphImage);
            view.setImageBitmap(project.getMorph().getFirst().getBitmap());
        }

        TextView label = (TextView) activity.findViewById(R.id.frameLabel);
        if (frameCount == 0) {
            label.setText("Frame: 0 of 0");
        } else {
            label.setText("Frame: " + (index + 1) + " of " + frameCount);
        }

        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.prevButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevImage();
            }
        });

        fab = (FloatingActionButton) activity.findViewById(R.id.nextButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.morph_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.morph:
                DialogFragment newFragment = new FrameSelectDialogFragment();
                newFragment.show(getFragmentManager(), "selectFrames");
                return true;
            case R.id.play_forward:
                if (!isPlayingReverse) {
                    isPlayingForward = !isPlayingForward;
                    MenuItem playForward = menu.findItem(R.id.play_forward);
                    if (isPlayingForward) {
                        playForward.setTitle("Stop");
                    } else {
                        playForward.setTitle("Forward");
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            while (isPlayingForward) {
                                try {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nextImage();
                                        }
                                    });
                                    sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }.start();
                }
                return true;
            case R.id.play_reverse:
                if (!isPlayingForward) {
                    isPlayingReverse = !isPlayingReverse;
                    MenuItem playReverse = menu.findItem(R.id.play_reverse);
                    if (isPlayingReverse) {
                        playReverse.setTitle("Stop");
                    } else {
                        playReverse.setTitle("Reverse");
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            while (isPlayingReverse) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            prevImage();
                                        }
                                    });
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {}
                            }
                        }
                    }.start();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void nextImage() {
        index++;
        int frameCount = project.getMorph().size();
        ImageView view = (ImageView) activity.findViewById(R.id.morphImage);
        if (index >= project.getMorph().size()) {
            index = 0;
        }
        if (project.getMorph().size() > 0 && index < project.getMorph().size()) {
            view.setImageBitmap(project.getMorph().get(index).getBitmap());
        }

        TextView label = (TextView) activity.findViewById(R.id.frameLabel);
        if (frameCount == 0) {
            label.setText("Frame: 0 of 0");
        } else {
            label.setText("Frame: " + (index + 1) + " of " + frameCount);
        }
    }

    public void prevImage() {
        index--;
        int frameCount = project.getMorph().size();
        ImageView view = (ImageView) activity.findViewById(R.id.morphImage);
        if (index < 0) {
            index = frameCount - 1;
        }
        if (project.getMorph().size() > 0 && index < project.getMorph().size()) {
            view.setImageBitmap(project.getMorph().get(index).getBitmap());
        }

        TextView label = (TextView) activity.findViewById(R.id.frameLabel);
        if (frameCount == 0) {
            label.setText("Frame: 0 of 0");
        } else {
            label.setText("Frame: " + (index + 1) + " of " + frameCount);
        }
    }
}
