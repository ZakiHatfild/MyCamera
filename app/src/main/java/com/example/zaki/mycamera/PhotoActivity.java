package com.example.zaki.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.example.zaki.mycamera.FileWork.FileWork;
import com.example.zaki.mycamera.SQLite.WorkSQLite;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.zaki.mycamera.TimeWork.DateTime.getTime;

public class PhotoActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    Camera camera = null;
    MediaRecorder mediaRecorder;

    File pictures;

    Boolean cameraEnabled = false;

    private void getImageSurface() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        pictures = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else {
            cameraEnabled = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawer dr = new Drawer()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.photoCamera).withIcon(FontAwesome.Icon.faw_camera).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.videoCamera).withIcon(FontAwesome.Icon.faw_video_camera).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.myPhotos).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.myVideos).withIcon(FontAwesome.Icon.faw_bitcoin).withIdentifier(4)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) PhotoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(PhotoActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                Intent photoIntent = new Intent(view.getContext(), PhotoActivity.class);
                                startActivity(photoIntent);
                                break;
                            case 2:
                                Intent videoIntent = new Intent(view.getContext(), VideoActivity.class);
                                startActivity(videoIntent);
                                break;
                            case 3:
                                Intent photoFiles = new Intent(view.getContext(), FileActivity.class);
                                photoFiles.putExtra("dir", "pic");
                                startActivity(photoFiles);
                                break;
                            case 4:
                                Intent videoFiles = new Intent(view.getContext(), FileActivity.class);
                                videoFiles.putExtra("dir", "vid");
                                startActivity(videoFiles);
                                break;
                            default: break;
                        }
                    }
                });
        dr.withSelectedItem(0);
        dr.build();


        if (cameraEnabled) getImageSurface();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraEnabled = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraEnabled) {
            camera = Camera.open();
            getImageSurface();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEnabled) {
            releaseMediaRecorder();
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
                surfaceView = null;
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }

    public void onClickTakePhoto(View view) {
        if (cameraEnabled) {
            camera.takePicture(null, null, new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        String fileName = getTime().replaceAll(" ", "") + ".JPG";

                        File file = new File(pictures.getAbsolutePath()+File.separator+fileName);
                        if (!file.exists()) file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(data);
                        fos.close();

                        //releaseMediaRecorder();
                        //camera = Camera.open();
                        //getImageSurface();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
