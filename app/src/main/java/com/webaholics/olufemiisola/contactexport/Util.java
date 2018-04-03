package com.webaholics.olufemiisola.contactexport;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Xml;

import org.json.JSONArray;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Olufemi Isola on 19/11/17.
 */

public class Util {

    /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }



    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }else {
            return false;
        }
    }

    public static String getCSV(List<ContactObject> contacts){
        String newLine = System.lineSeparator();

        String csvRow ="ID\tDisplayName\tFirstName\tLastName\tPhone\tAddress\tEmail";
        for (ContactObject obj :contacts) {
            csvRow += (newLine);
            String newRow = String.format("%1s \t %2s  \t %3s \t %4s \t %5s \t %6s \t %7s",obj.get_ID(),obj.get_displayName(),obj.get_firstName(),obj.get_lastName(),obj.get_phone(),obj.get_address(),obj.get_email());
            csvRow += (newRow);
        }
        return  csvRow;
    }

    public static String getJSON(List<ContactObject> contacts){
        String newLine = System.lineSeparator();
        JSONArray jsonArray = new JSONArray();
        String csvRow ="";

        for (int i=0; i < contacts.size(); i++) {
            jsonArray.put(contacts.get(i).getJSONObject());
        }
//        for (ContactObject obj :contacts) {
//            csvRow += (newLine);
//            String newRow = String.format("%1s \t %2s  \t %3s \t %4s \t %5s \t %6s \t %7s",obj.get_ID(),obj.get_displayName(),obj.get_firstName(),obj.get_lastName(),obj.get_phone(),obj.get_address(),obj.get_email());
//            csvRow += (newRow);
//        }
        csvRow = jsonArray.toString();
        return  csvRow;
    }

    public static String getXml(List<ContactObject> contacts){
            XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Contacts");
            serializer.attribute("", "Number", String.valueOf(contacts.size()));
            for (ContactObject contact: contacts){
                serializer.startTag("", "Contact");
                serializer.startTag("", "DisplayName");
                serializer.text(contact.get_displayName());
                serializer.endTag("", "DisplayName");
                serializer.startTag("", "FirstName");
                serializer.text(contact.get_firstName());
                serializer.endTag("", "FirstName");
                serializer.startTag("", "LastName");
                serializer.text(contact.get_lastName());
                serializer.endTag("", "LastName");
                serializer.startTag("", "Phone");
                serializer.text(contact.get_phone());
                serializer.endTag("", "Phone");
                serializer.startTag("", "Address");
                serializer.text(contact.get_address());
                serializer.endTag("", "Address");
                serializer.startTag("", "Email");
                serializer.text(contact.get_email());
                serializer.endTag("", "Email");
                serializer.endTag("", "Contact");
            }
            serializer.endTag("", "Contacts");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasPermissions (Context context,String... permissions){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null){
                for(String permission:permissions){
                    if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                        return  false;
                    }
                }
            }
            return  true;
    }


    public static boolean checkAndRequestPermissions (Context context, int requestCode){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null ){
            int readContactPermission =  ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
            int writeFilesPermission =  ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List <String> permissionsNeeded =  new ArrayList<String>();
            if(readContactPermission != PackageManager.PERMISSION_GRANTED){
                permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
            }
            if(writeFilesPermission != PackageManager.PERMISSION_GRANTED){
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(!permissionsNeeded.isEmpty()){
                ActivityCompat.requestPermissions((Activity)context,permissionsNeeded.toArray(new String[permissionsNeeded.size()]),requestCode);
                return false;
            }
        }
        return  true;
    }
    public static List<ExportItem> getMyFiles(String filesFolder,Context context){
        List<ExportItem> filesList =  new ArrayList<ExportItem>();

        String path = filesFolder;
        //Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null) {
            //Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++) {
                // Log.d("Files", "FileName:" + files[i].getName());
                ExportItem fileItem = new ExportItem(context);
                fileItem.set_name(files[i].getName());
                fileItem.set_path(files[i].getAbsolutePath());
                fileItem.set_sizeKB((int)files[i].length()/1000);
                String ext =  files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("."));
                fileItem.set_extension(ext);
                int drawable = R.drawable.icons8_find_user_male_48;
                switch (ext){
                    case ".json":
                        drawable = R.drawable.icons8_json_48;

                        break;
                    case ".xml":
                        drawable = R.drawable.icons8_xml_48;
                        break;
                    case ".csv":
                        drawable = R.drawable.icons8_csv_48;
                        break;
                    case".xl":
                        drawable = R.drawable.icons8_microsoft_excel_48;
                        break;
                    default:
                        drawable = R.drawable.icons8_find_user_male_48;
                        break;
                }
                fileItem.set_drawableIcon(drawable);
                filesList.add(fileItem);
            }
        }
        return filesList;
    }

    public static List<ExportedItem> getExportedItems(String filesFolder){
        List<ExportedItem> filesList =  new ArrayList<ExportedItem>();

        String path = filesFolder;
        //Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null) {
            //Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++) {
                // Log.d("Files", "FileName:" + files[i].getName());
                ExportedItem fileItem = new ExportedItem();
                fileItem.set_name(files[i].getName());
                fileItem.set_path(files[i].getAbsolutePath());
                fileItem.set_sizeKB((int)files[i].length()/1000);
                String ext =  files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("."));
                fileItem.set_extension(ext);
                int drawable = R.drawable.icons8_find_user_male_48;
                switch (ext){
                    case ".json":
                        drawable = R.drawable.icons8_json_48;

                        break;
                    case ".xml":
                        drawable = R.drawable.icons8_xml_48;
                        break;
                    case ".csv":
                        drawable = R.drawable.icons8_csv_48;
                        break;
                    case".xl":
                        drawable = R.drawable.icons8_microsoft_excel_48;
                        break;
                    default:
                        drawable = R.drawable.icons8_find_user_male_48;
                        break;
                }
                fileItem.set_drawableIcon(drawable);
                filesList.add(fileItem);
            }
        }
        return filesList;
    }

    @NonNull
    public static String  saveToFile(String filename, String content, Context context){
        if(isExternalStorageWritable()){
            String WebaholicsFolder = "ContactExport";
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, WebaholicsFolder);
            if (!file.exists()) {
                file.mkdirs();
            }


            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date now = new Date();
            String fileNameAppendage = formatter.format(now);

            String path = file.getAbsolutePath()+ "/" +fileNameAppendage+filename;
            File theFile = new File(path);
           // File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileNameAppendage+filename);
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(theFile);//context.openFileOutput(filename, Context.Mode);
                outputStream.write(content.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  "";
    }

}


