package ca.bcit.comp4932.morphtool;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 2018-01-30.
 */

public class FileSelectorActivity extends ListActivity {
    private List<String> item = null;
    private List<String> path = null;
    private String root;
    private TextView myPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);

        myPath = (TextView)findViewById(R.id.path);
        root = getFilesDir().getAbsolutePath();
        getDir(root);
    }

    private void getDir(String dirPath) {
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory()) {
                item.add(file.getName() + "/");
            } else {
                item.add(file.getName());
            }
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_selector_row, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        final File file = new File(path.get(position));

        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(path.get(position));
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setTitle("[" + file.getName() + "] folder can't be read")
                        .setPositiveButton("OK", null).show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setTitle(file.getName())
                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Intent data = new Intent();
                            data.setData(Uri.fromFile(file));
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                                String dir = file.getParent();
                                file.delete();
                                getDir(dir);
                        }
                    }).show();
        }
    }
}
