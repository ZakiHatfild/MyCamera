package com.example.zaki.mycamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.zaki.mycamera.FileWork.BoxAdapter;
import com.example.zaki.mycamera.FileWork.FileWork;
import com.example.zaki.mycamera.Models.FileModel;
import com.example.zaki.mycamera.SQLite.WorkSQLite;

import java.util.ArrayList;

public class FileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static BoxAdapter adapterItems;
    private ListView lvItems;

    static String dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hey", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        dir = getIntent().getStringExtra("dir");

        ArrayList<FileModel> items = FileWork.getFiles(dir);

        adapterItems = new BoxAdapter(getApplicationContext(), items, dir);
        lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setAdapter(adapterItems);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            FileWork.deleteFiles(WorkSQLite.GetPoints(this));
            return true;
        } else if (id == R.id.upload) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.PhotoCamera) {
            //android.app.FragmentManager fragmentManager = getFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new ).commit();
            Intent photo = new Intent(this, PhotoActivity.class);
            startActivity(photo);
        } else if (id == R.id.VideoCamera) {
            Intent video = new Intent(this, VideoActivity.class);
            startActivity(video);
        } else if (id == R.id.MyPhotos) {
            Intent myPhotos = new Intent(this, FileActivity.class);
            myPhotos.putExtra("dir", "pic");
            startActivity(myPhotos);
        } else if (id == R.id.MyVideos) {
            Intent myVideos = new Intent(this, FileActivity.class);
            myVideos.putExtra("dir", "vid");
            startActivity(myVideos);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
