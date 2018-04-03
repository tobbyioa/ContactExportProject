package com.webaholics.olufemiisola.contactexport;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Olufemi Isola on 5/11/17.
 */

public class QuickViewDetails implements Serializable {
    private Uri _contactUri;
    private String _contactThumbNailUri;

    public Uri get_contactUri() {
        return _contactUri;
    }

    public void set_contactUri(Uri _contactUri) {
        this._contactUri = _contactUri;
    }

    public String get_contactThumbNailUri() {
        return _contactThumbNailUri;
    }

    public void set_contactThumbNailUri(String _contactThumbNailUri) {
        this._contactThumbNailUri = _contactThumbNailUri;
    }
}
