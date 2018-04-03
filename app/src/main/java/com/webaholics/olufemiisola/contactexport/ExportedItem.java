package com.webaholics.olufemiisola.contactexport;

/**
 * Created by Olufemi Isola on 25/02/18.
 */

public class ExportedItem {
    private String _name;
    private String _path;
    private int _drawableIcon;
    private  String _extension;
    private int _sizeKB ;
    private boolean active;

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
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
