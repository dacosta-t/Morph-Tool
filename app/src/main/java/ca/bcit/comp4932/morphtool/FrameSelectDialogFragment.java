package ca.bcit.comp4932.morphtool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Tyler on 2018-01-24.
 */

public class FrameSelectDialogFragment extends DialogFragment {

    MainActivity activity;

    public Dialog onCreateDialog(Bundle savedInstance) {
        activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Set up the input
        final EditText input = new EditText(activity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setMessage("Input the amount of intermittent frames.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        int frameCount = Integer.parseInt(input.getText().toString());
                        startMorph(frameCount);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void startMorph(final int frames) {
        final Project project = activity.getProject();
        final MorphThread forwardMorph = new MorphThread(activity, project.getLeftImage(), project.getRightImage(), project.getLines(), frames, true);
        final MorphThread reverseMorph = new MorphThread(activity, project.getRightImage(), project.getLeftImage(), project.reverseLines(), frames, false);
        forwardMorph.start();
        reverseMorph.start();
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    android.util.Log.d("Before", "Waiting");

                    forwardMorph.join();
                    reverseMorph.join();
                    Morpher morph = new Morpher();

                    project.setMorph(morph.crossDissolve(forwardMorph.getMorph(), reverseMorph.getMorph()));

                    android.util.Log.d("After", "Done");
                } catch (Exception e) {}
            }
        };
        th.start();

    }
}
