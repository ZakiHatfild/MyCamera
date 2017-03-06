package com.example.zaki.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    final int CAMERA_ID = 0;
    final boolean FULL_SCREEN = true;

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
                camera.stopPreview();
                setCameraDisplayOrientation(CAMERA_ID);
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo);


        pictures = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else {
            cameraEnabled = true;
        }

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawer dr = new Drawer()
                .withActivity(this)
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
            setPreviewSize(FULL_SCREEN);
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

            new Thread(new Runnable() {
                public void run() {
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.photo_click);
                    mp.start();
                }
            }).start();

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

    void setPreviewSize(boolean fullScreen) {

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Camera.Size size = camera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay, Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        surfaceView.getLayoutParams().height = (int) (rectPreview.bottom);
        surfaceView.getLayoutParams().width = (int) (rectPreview.right);
    }

    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);
    }

}
