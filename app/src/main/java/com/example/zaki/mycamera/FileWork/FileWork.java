package com.example.zaki.mycamera.FileWork;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.zaki.mycamera.Models.FileModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Zaki on 3/2/2017.
 */

public class FileWork {

    /*public static class getFiles extends AsyncTask<String, Void, ArrayList<FileModel>> {

        @Override
        protected ArrayList<FileModel> doInBackground(String... params) {
            File rootFolder;
            ArrayList<FileModel> files = new ArrayList<>();

            switch (params[0]) {
                case "pic": rootFolder = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); break;
                case "vid": rootFolder = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES); break;
                default: rootFolder = null; break;
            }

            File[] filesArray = rootFolder.listFiles();

            for (File f: filesArray) {
                if (f.isFile()) files.add(new FileModel(f.getName(), f.getPath()));
            }

            return files;
        }

       @Override
       protected void onPostExecute(ArrayList<FileModel> value) {
            super.onPostExecute(value);
       }
    }*/

    public static void deleteFiles(ArrayList<FileModel> files) {

        File PicFolder = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File VidFolder = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        File[] videos = VidFolder.listFiles();
        File[] pictures = PicFolder.listFiles();

        for (FileModel file: files) {
            for (int i = 0; i < videos.length; i++) {
                if (videos[i].getName().equals(file.name)) videos[i].delete();
            }
            for (int j = 0; j < pictures.length; j++) {
                if (pictures[j].getName().equals(file.name)) pictures[j].delete();
            }
        }
    }
}
