package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Olufemi Isola on 12/11/17.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    private Cursor _cursor;
    private Context _context;
    private String[] _from;
    private int[] _to;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        _cursor = c;
        _context = context;
        _from = from;
        _to = to;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int id = cursor.getColumnIndex(ContactsContract.Data._ID);

        String lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

        TextView txt = (TextView)view.findViewById(R.id.contactName);
        txt.setText(displayName);
        String d = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            d = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
            // Otherwise, sets the thumbnail column to the _ID column
        } else {
            d = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
        }
        QuickContactBadge badgeSmall = (QuickContactBadge) view.findViewById(R.id.quickContactBadge);
        if(d != null) {
            Uri photo_uri = Uri.parse(d);

            //Bitmap mThumbnail = loadContactPhotoThumbnail(view,d);
            badgeSmall.setImageURI(photo_uri);
           // badgeSmall.setImageBitmap(mThumbnail);
            badgeSmall.setMode(ContactsContract.QuickContact.MODE_LARGE);
        }
        badgeSmall.assignContactUri(ContactsContract.Contacts.getLookupUri(id, lookUpKey));
        //if(badgeSmall.!=null) badgeSmall.setImageDrawable(bitmap);
        super.bindView(view, context, cursor);
    }

    private Bitmap loadContactPhotoThumbnail(View view,String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            // If Android 3.0 or later
            if (Build.VERSION.SDK_INT
                    >=
                    Build.VERSION_CODES.HONEYCOMB) {
                // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                thumbUri = Uri.parse(photoData);
            } else {
                // Prior to Android 3.0, constructs a photo Uri using _ID
                /*
                 * Creates a contact URI from the Contacts content URI
                 * incoming photoData (_ID)
                 */
                final Uri contactUri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI, photoData);
                /*
                 * Creates a photo URI by appending the content URI of
                 * Contacts.Photo.
                 */
                thumbUri =
                        Uri.withAppendedPath(
                                contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = view.getContext().getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }

}
