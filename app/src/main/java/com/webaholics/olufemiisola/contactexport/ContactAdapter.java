package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Olufemi Isola on 30/03/18.
 */

public class ContactAdapter extends SelectableAdapter<ContactAdapter.ViewHolder>{

    private Cursor data;
    private Context context;
    private int selectedItem = 0;
    private SparseBooleanArray selectedItems;


    private ContactAdapter.ViewHolder.ClickListener clickListener;


    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {
        //ContactObject item = data.get(position);
        data.moveToPosition(position);
        String pd = null;
        ContactObject item = new ContactObject();
        item.set_ID(data.getString( data.getColumnIndexOrThrow(ContactsContract.Data._ID)));
        item.set_lookUpKey(data.getString(data.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)));
        // item = retrieveLookUpKey(context,item);
        if(Util.hasHoneycomb()){
            item.set_displayName(data.getString( data.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
        }else{
            item.set_displayName(data.getString( data.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)));
        }

        if (Util.hasHoneycomb()) {
            pd = (data.getString(data.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
            // Otherwise, sets the thumbnail column to the _ID column
        }
        else {
            pd = data.getString(data.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
        }

        item.set_photoUri(pd);

        int id = Integer.parseInt(item.get_ID());
        String lookUpKey = item.get_lookUpKey();
        String displayName = item.get_displayName();

        holder.contactName.setText(displayName);
        //holder.exportIcon.setVisibility(View.VISIBLE);

        if(item.get_photoUri() != null) {
            Uri photo_uri = Uri.parse(item.get_photoUri());

            //Bitmap mThumbnail = loadContactPhotoThumbnail(view,d);
            holder.badge.setImageURI(photo_uri);
            // badgeSmall.setImageBitmap(mThumbnail);
            holder.badge.setMode(ContactsContract.QuickContact.MODE_LARGE);

            holder.badge.assignContactUri(ContactsContract.Contacts.getLookupUri(id, lookUpKey));
        }
//        else{
//           String defaultValue =  String.valueOf(R.drawable.user_96);
//            Bitmap mThumbnail = loadContactPhotoThumbnail(defaultValue);
//            holder.badge.setMode(ContactsContract.QuickContact.MODE_LARGE);
//            holder.badge.setImageBitmap(mThumbnail);
//        }


        // Span the item if active
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof RecyclerView.LayoutParams) {
            RecyclerView.LayoutParams sglp = (RecyclerView.LayoutParams) lp;
            // sglp.setFullSpan(item.isActive());
            holder.itemView.setLayoutParams(lp);
        }

        // Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        if (data == cursor) {
            return null;
        }
        Cursor oldCursor = data;
        this.data = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public ContactAdapter(Cursor data, Context context, ViewHolder.ClickListener clickListener) {
        this.data = data;
        this.context = context;
        this.clickListener = clickListener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View exportedItem = inflater.inflate(R.layout.contact_item,parent,false);
        ContactAdapter.ViewHolder holder = new ContactAdapter.ViewHolder(exportedItem,clickListener);
        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        @SuppressWarnings("unused")
        private  final String TAG = ContactAdapter.ViewHolder.class.getSimpleName();
        public TextView contactName;
        public TextView id;
        public TextView lookUpKey;
        public QuickContactBadge badge;
        public View selectedOverlay;
        private ContactAdapter.ViewHolder.ClickListener listener;

        public ViewHolder(View itemView , ClickListener listener) {
            super(itemView);

            contactName = (TextView)itemView.findViewById(R.id.Name);
            id = (TextView)itemView.findViewById(R.id.id);
            lookUpKey = (TextView)itemView.findViewById(R.id.lookUpKey);
            badge = (QuickContactBadge) itemView.findViewById(R.id.Badge);

            selectedOverlay = itemView.findViewById(R.id.contact_selected_overlay);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Item clicked at position " + getPosition());
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            Log.d(TAG, "Item long-clicked at position " + getPosition());
            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }
    }


    /**
     * Load a contact photo thumbnail and return it as a Bitmap,
     * resizing the image to the provided image dimensions as needed.
     * @param photoData photo ID Prior to Honeycomb, the contact's _ID value.
     * For Honeycomb and later, the value of PHOTO_THUMBNAIL_URI.
     * @return A thumbnail Bitmap, sized to the provided width and height.
     * Returns null if the thumbnail is not found.
     */
    private Bitmap loadContactPhotoThumbnail(String photoData) {
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
            afd = this.getContext().getContentResolver().
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
