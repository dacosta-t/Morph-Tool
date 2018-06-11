package ca.bcit.comp4932.morphtool;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RESULT_LOAD_LEFT_IMG = 1;
    private static final int RESULT_LOAD_RIGHT_IMG = 2;
    private static final int RESULT_OPEN_PROJECT = 3;

    enum Views {
        EDIT,
        MORPH
    }

    private Project project;
    private Views currView;
    private int maxViewWidth;
    private int maxViewHeight;
    private boolean morphRunning;
    private int morphFrameCount;
    private int currentMorphFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerView.bringToFront();
                getWindow().getDecorView().requestLayout();
                getWindow().getDecorView().invalidate();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        project = new Project();
        currView = Views.EDIT;

        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new DrawFragment());
        tx.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.leftView);
        if (layout != null) {
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setMaxViewWidth(layout.getWidth());
                    setMaxViewHeight(layout.getHeight());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent gallery;
        FragmentTransaction tx;
        switch (id) {
            case R.id.draw_view:
                currView = Views.EDIT;
                tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, new DrawFragment());
                tx.commit();
                break;
            case R.id.morph_view:
                currView = Views.MORPH;
                tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, new MorphViewFragment());
                tx.commit();
                break;
            case R.id.new_project:
                recreate();
                break;
            case R.id.open_project:
                Intent open = new Intent(this, FileSelectorActivity.class);
                startActivityForResult(open, RESULT_OPEN_PROJECT);
                break;
            case R.id.save_project:
                saveProject();
                break;
            case R.id.import_left_image:
                if (currView == Views.EDIT) {
                    // Create intent to open image applications
                    gallery = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the intent
                    startActivityForResult(gallery, RESULT_LOAD_LEFT_IMG);
                }
                break;
            case R.id.import_right_image:
                if (currView == Views.EDIT) {
                    // Create intent to open image applications
                    gallery = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the intent
                    startActivityForResult(gallery, RESULT_LOAD_RIGHT_IMG);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == RESULT_LOAD_LEFT_IMG) {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    project.setLeftImage(bmp);
                    View view = findViewById(R.id.leftImage);
                    ((DrawSurfaceView)view).setBitmap(bmp);
                } else if (requestCode == RESULT_LOAD_RIGHT_IMG) {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    project.setRightImage(bmp);
                    View view = findViewById(R.id.rightImage);
                    ((DrawSurfaceView)view).setBitmap(bmp);
                } else if (requestCode == RESULT_OPEN_PROJECT) {
                    String path = data.getData().getPath();
                    String fileName = path.substring(path.lastIndexOf("/")+1);;
                    FileInputStream fis = this.openFileInput(fileName);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    project = (Project) is.readObject();
                    is.close();
                    fis.close();
                    FragmentTransaction tx = getFragmentManager().beginTransaction();
                    tx.replace(R.id.content_frame, new DrawFragment());
                    tx.commit();
                }

            } catch (Exception e) {}
        }
    }

    public Project getProject() {
        return project;
    }

    public void setMaxViewWidth(int width) {
        maxViewWidth = width;
    }

    public void setMaxViewHeight(int height) {
        maxViewHeight = height;
    }

    public int getMaxViewWidth() {
        return maxViewWidth;
    }

    public int getMaxViewHeight() {
        return maxViewHeight;
    }

    public void saveProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Input the name of the project.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String projectName = input.getText().toString() + ".mrph";
                        if (!projectName.isEmpty()) {
                            project.saveProject(MainActivity.this, projectName);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
