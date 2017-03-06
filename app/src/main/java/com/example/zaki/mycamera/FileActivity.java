package com.example.zaki.mycamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zaki.mycamera.FileWork.BoxAdapter;
import com.example.zaki.mycamera.FileWork.FileWork;
import com.example.zaki.mycamera.Models.FileModel;
import com.example.zaki.mycamera.SQLite.WorkSQLite;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FileActivity extends ActionBarActivity {

    static String dir;

    ArrayList<FileModel> searchList;
    BoxAdapter adapterItems;

    private ListView lvItems;


    public class LoadFiles extends AsyncTask<String, FileModel, Void> {

        @Override
        protected Void doInBackground(String... params) {

            File rootFolder;
            File[] filesArray;

            switch (params[0]) {
                case "pic": rootFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            filesArray = rootFolder.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    return name.toLowerCase().endsWith(".jpg");
                                }
                            }); break;
                case "vid": rootFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                            filesArray = rootFolder.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    return name.toLowerCase().endsWith(".mp4");
                                }
                            }); break;
                default: rootFolder = null; filesArray = null; break;
            }



            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            for (File f: filesArray) {
                if (f.isFile()) {

                    FileModel file;

                    switch (params[0]) {
                        case "pic": file = new FileModel(f.getName(), f.getPath(), BitmapFactory.decodeFile(f.getPath(), options)); break;
                        case "vid": file = new FileModel(f.getName(), f.getPath(), ThumbnailUtils.createVideoThumbnail(f.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND)); break;
                        default: file = null; break;
                    }

                    publishProgress(file);

                    try {
                        Thread.currentThread();
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(FileModel... values) {
            if (values[0] != null) adapterItems.setListItems(values[0]);
            //adapterItems.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawer dr = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) FileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(FileActivity.this.getCurrentFocus().getWindowToken(), 0);
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

        dir = getIntent().getStringExtra("dir");

        switch (dir) {
            case "pic": dr.addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.photoCamera).withIcon(FontAwesome.Icon.faw_camera).withIdentifier(1),
                            new PrimaryDrawerItem().withName(R.string.videoCamera).withIcon(FontAwesome.Icon.faw_video_camera).withIdentifier(2),
                            new PrimaryDrawerItem().withName(R.string.myPhotos).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3),
                            new PrimaryDrawerItem().withName(R.string.myVideos).withIcon(FontAwesome.Icon.faw_bitcoin).withIdentifier(4)
                        ); dr.withSelectedItem(2); break;
            case "vid": dr.addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.photoCamera).withIcon(FontAwesome.Icon.faw_camera).withIdentifier(1),
                            new PrimaryDrawerItem().withName(R.string.videoCamera).withIcon(FontAwesome.Icon.faw_video_camera).withIdentifier(2),
                            new PrimaryDrawerItem().withName(R.string.myPhotos).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3),
                            new PrimaryDrawerItem().withName(R.string.myVideos).withIcon(FontAwesome.Icon.faw_bitcoin).withIdentifier(4)
                        ); dr.withSelectedItem(3); break;
        }

        dr.build();

        searchList = new ArrayList<FileModel>();
        adapterItems = new BoxAdapter(this, searchList);

        lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setAdapter(adapterItems);

        new LoadFiles().execute(dir);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {

            ArrayList<FileModel> files = WorkSQLite.GetPoints(this);
            for (FileModel file: files) {
                WorkSQLite.DeletePoint(this, file);
            }

            FileWork.deleteFiles(files);
            adapterItems.deleteListItems(files);

            return true;
        } else if (id == R.id.upload) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
