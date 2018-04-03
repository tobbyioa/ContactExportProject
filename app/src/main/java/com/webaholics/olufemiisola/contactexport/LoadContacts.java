package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.greenrobot.eventbus.EventBus.TAG;

/**
 * Created by Olufemi Isola on 26/11/17.
 */

public class LoadContacts {

   private String[] projection = {
           ContactsContract.Data._ID,
           ContactsContract.Contacts.LOOKUP_KEY,
           ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
           Build.VERSION.SDK_INT
                   >= Build.VERSION_CODES.HONEYCOMB ?
                   ContactsContract.Data.DISPLAY_NAME_PRIMARY :
                   ContactsContract.Data.DISPLAY_NAME
    };


    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = '1'" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = '1'";
    private String mSearchString = "";
    private String[] mSelectionArgs = { mSearchString };

    public List<ContactObject> GetAllContacts (Context context){
        ArrayList<ContactObject> items = new ArrayList<ContactObject>();
        Uri lookUpUri = ContactsContract.Data.CONTENT_URI;
        String[] projection = {
                ContactsContract.Data._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Data.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Data.DISPLAY_NAME
        };
       // String selection = ContactsContract.Data.LOOKUP_KEY + "=?";
        //String[] selectionArgs = new String[]{};
        String d = null;
        CursorLoader cursorLoader_LookUp = new CursorLoader(
                context,
                lookUpUri,
                projection,
                SELECTION,
                mSelectionArgs,
                null);
        Cursor cursor = cursorLoader_LookUp.loadInBackground();

        while(cursor.moveToNext()){
            ContactObject item = new ContactObject();
            item.set_ID(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data._ID)));
            item.set_lookUpKey(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)));
            // item = retrieveLookUpKey(context,item);
            if(Util.hasHoneycomb()){
                item.set_displayName(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
            }else{
                item.set_displayName(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)));
            }

            if (Util.hasHoneycomb()) {
                d = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
                // Otherwise, sets the thumbnail column to the _ID column
            } else {
                d = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
            }

            item.set_photoUri(d);
        }
        return  items;
    }


   public List<ContactObject> GetContacts (Context context,String searchFilter){
       ArrayList<ContactObject> items = new ArrayList<ContactObject>();
       String d = null;
       this.mSearchString = searchFilter;
       mSelectionArgs[0] = "%" + mSearchString + "%";
       Cursor cursor =  context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,projection,SELECTION,mSelectionArgs,null);

       while(cursor.moveToNext()) {
           ContactObject item = new ContactObject();
           item.set_ID(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data._ID)));
          item.set_lookUpKey(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)));
          // item = retrieveLookUpKey(context,item);
           if(Util.hasHoneycomb()){
              item.set_displayName(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
           }else{
               item.set_displayName(cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)));
           }

           if (Util.hasHoneycomb()) {
               d = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
               // Otherwise, sets the thumbnail column to the _ID column
           } else {
               d = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
           }

           item.set_photoUri(d);

            item = retrieveContactAddress(context,item);
           item = retrieveContactNumber(context,item);
           item = retrieveContactEmailAddress(context,item);
           item = retrieveContactName(context,item);
           items.add(item);
       }
       cursor.close();
       return  items ;

   }
//    private ContactObject retrieveLookUpKey(Context context, ContactObject contact) {
//        String projection = String[] { ContactsContract.Contacts.LOOKUP_KEY };
//        String lookupKey = "";
//        Cursor cursor = context.getContentResolver().query(
//                ContactsContract.Data.CONTENT_URI,
//                null,
//                ContactsContract.Data._ID + " = "
//                        + contact.get_ID(), null, null);
//
//        if (cursor != null && cursor.moveToNext()) {
//            lookupKey = cursor.getString(0);
//            Log.d(TAG, "key = " + lookupKey);
//           // cursor.close();
//        }
//        contact.set_address(lookupKey);
//        cursor.close();
//
//        return  contact;
//    }
    private ContactObject retrieveContactAddress(Context context, ContactObject contact) {


        String address  = null;
        // Using the contact ID now we will get contact address
//        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
//                new String[]{
//                ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
//    ContactsContract.CommonDataKinds.StructuredPostal.STREET,
//    ContactsContract.CommonDataKinds.StructuredPostal.CITY,
//    ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
//    ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,},
//                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ? AND " +
//                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME + " OR "+
//                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK ,
//
//                new String[]{contact.get_ID()},
//                null);

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = "
                        + contact.get_ID(), null, null);

        int postFormattedNdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
        int postTypeNdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
        int postStreetNdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);

        if (cursor.moveToFirst()) {

//            String po = cursor.getString( cursor.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//            String street = cursor.getString( cursor.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//            String city = cursor.getString( cursor.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//            String country = cursor.getString( cursor.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//            String postcode = cursor.getString( cursor.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
//            address = String.format("%1s | %2s  | %3s | %4s | %5s ",po,street,city,country,postcode);

            String postalData = cursor.getString(postFormattedNdx);
            address = address+ postalData+ ", [";
            postalData = String.valueOf(cursor.getInt(postTypeNdx));
            address = address+ postalData+ "], ";
            postalData = cursor.getString(postStreetNdx);
            address = cursor+ postalData+ " ";

        }

        contact.set_address(address);
        cursor.close();

        return  contact;
    }

    private ContactObject retrieveContactNumber(Context context, ContactObject contact) {


        String contactNumber = null;
        // Using the contact ID now we will get contact phone number
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contact.get_ID()},
                null);

        if (cursor.moveToFirst()) {
            contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        contact.set_phone(contactNumber);

        cursor.close();

        return  contact;
    }

    private ContactObject retrieveContactEmailAddress(Context context, ContactObject contact) {


        String email = null;
        // Using the contact ID now we will get contact email address
//        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? AND " +
//                        ContactsContract.CommonDataKinds.Email.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.Email.TYPE_MOBILE+" OR "+
//                        ContactsContract.CommonDataKinds.Email.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.Email.TYPE_HOME+" OR "+
//                        ContactsContract.CommonDataKinds.Email.TYPE + " = " +
//                        ContactsContract.CommonDataKinds.Email.TYPE_WORK+" OR "+
//                        ContactsContract.CommonDataKinds.Email.TYPE+ " = " +
//                        ContactsContract.CommonDataKinds.Email.TYPE_OTHER,
//
//                new String[]{contact.get_ID()},
//                null);

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contact.get_ID()}, null);

        if (cursor.moveToFirst()) {
            //email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        contact.set_email(email);

        cursor.close();

        return  contact;
    }

    private ContactObject retrieveContactName(Context context, ContactObject contact) {


        String famName = null;
        String givenName = null;

        String whereName = ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        while (cursor.moveToNext()) {
            String id = cursor.getString( cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
            if(id == contact.get_ID()){
                famName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            }
//            String given = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
//            String family = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
//            String display = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
        }
     //   cursor.close();

        // Using the contact ID now we will get contact phone number
//        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                new String[]{ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
//                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME},
//                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ? ",
//                new String[]{contact.get_ID()},
//                null);
//
//        if (cursor.moveToFirst()) {
//            famName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
//            givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
//        }
        contact.set_lastName(famName);
        contact.set_firstName(givenName);

        cursor.close();

        return  contact;
    }





}
