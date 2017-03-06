package com.example.zaki.mycamera.Models;

import android.graphics.Bitmap;

/**
 * Created by Zaki on 3/2/2017.
 */

public class FileModel {
    public String name;
    public String path;
    public Bitmap image;

    public FileModel(String _name, String _path, Bitmap _image) {
        name = _name;
        path = _path;
        image = _image;
    }

    public FileModel(String _name, String _path) {
        name = _name;
        path = _path;
        image = null;
    }
}
