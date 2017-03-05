package com.example.zaki.mycamera.FileWork;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zaki.mycamera.Models.FileModel;
import com.example.zaki.mycamera.R;
import com.example.zaki.mycamera.SQLite.WorkSQLite;

import java.util.ArrayList;

/**
 * Created by Zaki on 10/19/2016.
 */

public class BoxAdapter extends BaseAdapter {

    Context ctx;

    LayoutInflater lInflater;
    static ArrayList<FileModel> objects;
    static BoxAdapter adapter;
    static String dir;


    public BoxAdapter(Context context, ArrayList<FileModel> files, String _dir) {
        ctx = context;
        objects = files;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = this;
        dir = _dir;
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public FileModel getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.file_item, parent, false);
        }

        FileModel p = getFileModel(position);

        try {
            ((TextView) view.findViewById(R.id.name)).setText(p.name);
            ((TextView) view.findViewById(R.id.path)).setText(p.path);
        }
        catch (Exception ex) {
            Log.d("AreYouCrazyTam", ex.getMessage());
        }

        CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox);
        final FileModel find = p;

        if (WorkSQLite.isPointChecked(ctx, find)) cb.setChecked(true);
        else cb.setChecked(false);

        cb.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View t) {
                if (WorkSQLite.isPointChecked(ctx, find)) WorkSQLite.DeletePoint(ctx, find);
                else WorkSQLite.PutPoint(ctx, find);
            }
        });

        return view;
    }

    // товар по позиции
    FileModel getFileModel(int position) {
        return ((FileModel) getItem(position));
    }

    public static void update(Context context) {
        objects.clear();
        objects.addAll(FileWork.getFiles(dir));
        adapter.notifyDataSetChanged();
    }

    public static void clear() {
        objects.clear();
        adapter.notifyDataSetChanged();
    }

}