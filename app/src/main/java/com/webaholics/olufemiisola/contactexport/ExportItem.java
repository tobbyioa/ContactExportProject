package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Olufemi Isola on 11/02/18.
 */

public class ExportItem extends ConstraintLayout{
    private String _name;
    private String _path;
    private int _drawableIcon;
    private  String _extension;
    private int _sizeKB ;
    private ImageView fileType;
    private ImageView exportIcon;
    private TextView text;
    private ImageButton share;
    private String mimeType;

    public ExportItem(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        setupChildren();
    }
    public ExportItem(Context context) {
        super(context);
        setupChildren();
    }

    private void setupChildren() {
        this.setFileType((ImageView)findViewById(R.id.fileTypeImage));
        this.setExportIcon((ImageView)findViewById(R.id.imageView));
        this.setText((TextView) findViewById(R.id.fileName));
        this.setShare((ImageButton) findViewById(R.id.fileShareBtn));
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public ImageView getFileType() {
        return fileType;
    }

    public void setFileType(ImageView fileType) {
        this.fileType = fileType;
    }

    public ImageView getExportIcon() {
        return exportIcon;
    }

    public void setExportIcon(ImageView exportIcon) {
        this.exportIcon = exportIcon;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public ImageButton getShare() {
        return share;
    }

    public void setShare(ImageButton share) {
        this.share = share;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_path() {
        return _path;
    }

    public void set_path(String _path) {
        this._path = _path;
    }

    public int get_drawableIcon() {
        return _drawableIcon;
    }

    public void set_drawableIcon(int _drawableIcon) {
        this._drawableIcon = _drawableIcon;
    }

    public String get_extension() {
        return _extension;
    }

    public void set_extension(String _extension) {
        this._extension = _extension;
    }

    public int get_sizeKB() {
        return _sizeKB;
    }

    public void set_sizeKB(int _sizeKB) {
        this._sizeKB = _sizeKB;
    }

    public ExportItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ExportItem inflate(ViewGroup parent) {
        ExportItem itemView = (ExportItem) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exports_row, parent, false);
        itemView.setupChildren();
        return itemView;
    }


}
