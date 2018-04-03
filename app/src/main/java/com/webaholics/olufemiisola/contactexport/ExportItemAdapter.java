package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Olufemi Isola on 11/02/18.
 */

public class ExportItemAdapter extends ArrayAdapter<ExportItem> {

    private final LayoutInflater inflater;
    private Context context;

//    FloatingActionButton primaryFab;
//    FloatingActionButton shareFab;
//    FloatingActionButton deleteFab;
//    FloatingActionButton deleteSweepFab;



    public ExportItemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

    }

    public void setData(List<ExportItem> data) {
        clear();
        if (data != null) {
            for (ExportItem appEntry : data) {
                add(appEntry);
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ExportItem view = (ExportItem) convertView;
        if(view == null) {
            view = ExportItem.inflate(parent);
            //inflater.inflate(R.layout.exports_row,parent,false);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            view.getFileType().setImageDrawable(context.getResources().getDrawable(getItem(position).get_drawableIcon(), context.getTheme()));
        }else{
            view.getFileType().setImageDrawable(context.getResources().getDrawable(getItem(position).get_drawableIcon()));
        }
        view.getText().setText(getItem(position).get_name());
        view.getExportIcon().setVisibility(View.VISIBLE);
        return view;
        //return super.getView(position, convertView, parent);
    }
}
