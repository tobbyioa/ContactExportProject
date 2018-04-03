package com.webaholics.olufemiisola.contactexport;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ContactsList.OnFragmentInteractionListener {
 //,ExportsFragment.OnListFragmentInteractionListener
    private boolean isSearchResultView = false;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 300;
    //private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_ALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_export_main);

       // QuickContactBadge q =(QuickContactBadge)findViewById(R.id.quickContactBadge);
//        String[] PERMISSIONS ={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS};
//        if(!Util.hasPermissions(this,PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSIONS_ALL);
//        }

//        boolean hasPermissionReadContact = (ContextCompat.checkSelfPermission (this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
//        boolean hasPermissionWriteExternalStorage = (ContextCompat.checkSelfPermission (this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
//        if ( !hasPermissionReadContact || !hasPermissionWriteExternalStorage) {
//            List<String> permissions = new ArrayList<String>();
//
//            if(!hasPermissionReadContact){
//                permissions = new ArrayList<String>();
//                permissions.add(Manifest.permission.READ_CONTACTS);
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSIONS_REQUEST_READ_CONTACTS);
//            }
//
//            if(!hasPermissionWriteExternalStorage){
//                permissions = new ArrayList<String>();
//                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//            }
//
//        }
        if(Util.checkAndRequestPermissions(this,PERMISSIONS_ALL)) {
            if (findViewById(R.id.singleView) != null) {

                if (savedInstanceState != null) {
                    return;
                }
                ContactsList newFragment = new ContactsList();
                newFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.singleView, newFragment).commit();
            }

            // Check if this activity instance has been triggered as a result of a search query. This
            // will only happen on pre-HC OS versions as from HC onward search is carried out using
            // an ActionBar SearchView which carries out the search in-line without loading a new
            // Activity.
            if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

                // Fetch query from intent and notify the fragment that it should display search
                // results instead of all contacts.
                String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
                ContactsList mContactsListFragment = (ContactsList)
                        getSupportFragmentManager().findFragmentById(R.id.singleView);

                // This flag notes that the Activity is doing a search, and so the result will be
                // search results rather than all contacts. This prevents the Activity and Fragment
                // from trying to a search on search results.
                isSearchResultView = true;
                mContactsListFragment.setSearchQuery(searchQuery);

                // Set special title for search results
                String title = getString(R.string.contacts_list_search_results_title, searchQuery);
                setTitle(title);
            }
        }else{

            finish();
            startActivity(getIntent());
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSIONS_ALL:
//                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                  //  startActivity(getIntent());
//                }else{
//                    String perms = "";
//                    for(String per : permissions){
//                        perms += "\n"+per;
//                    }
//                }
                int x = 0;
                for (int i:grantResults) {
                    if(i !=PackageManager.PERMISSION_GRANTED){
                        String perms = permissions[x];
                        Toast.makeText(this, "You do not have permission: "+perms, Toast.LENGTH_SHORT).show();
                    }
                    x++;
                }
             //   return;
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode)
//        {
//            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    //reload my activity with permission granted or use the features what required the permission
//                    startActivity(getIntent());
//                } else
//                {
//                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                }
//            }
//            case PERMISSIONS_REQUEST_READ_CONTACTS: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    //reload my activity with permission granted or use the features what required the permission
//                    startActivity(getIntent());
//
//                } else
//                {
//                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                }
//            }
//        }

    }


    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return !isSearchResultView && super.onSearchRequested();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate( R.menu.search_contacts, menu);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            MenuItem vcfMenu = menu.findItem( R.id.contact_export_vcf);
           if(vcfMenu !=null){
               vcfMenu.setEnabled(false);
           }
            MenuItem xmlMenu = menu.findItem( R.id.contact_export_xml);
            if(xmlMenu !=null){
                xmlMenu.setEnabled(false);
            }
            MenuItem csvMenu = menu.findItem( R.id.contact_export_csv);
            if(csvMenu !=null){
                csvMenu.setEnabled(false);
            }
            MenuItem jsonMenu = menu.findItem( R.id.contact_export_json);
            if(jsonMenu !=null){
                jsonMenu.setEnabled(false);
            }
            MenuItem xlMenu = menu.findItem( R.id.contact_export_xl);
            if(xlMenu !=null){
                xlMenu.setEnabled(false);
            }
        }
        menu.findItem( R.id.contact_export_vcf).setVisible(false);
        MenuItem myActionMenuItem = menu.findItem( R.id.contactSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                EventBus.getDefault().post(new SearchQueryEvent(s));
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.contact_export_csv:
                Toast.makeText(this, "Exporting contacts to CSV...", Toast.LENGTH_SHORT).show();

                // Check the SDK version and whether the permission is already granted or not.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                } else {
                //getLoaderManager().initLoader(ContactsList.QUERY_ID, null, this);
                //Util.saveToFile("self.csv","hello,hi,welcome",this);

                new SaveContact(R.id.contact_export_csv).execute();

                //  }
                return true;
            case R.id.contact_export_xl:
                Toast.makeText(this, "Excel export selected", Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.contact_export_vcf:
//                Toast.makeText(this, "VCF export selected", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.contact_export_xml:
                Toast.makeText(this, "Exporting contacts to  XML...", Toast.LENGTH_SHORT).show();
                new SaveContact(R.id.contact_export_xml).execute();
                return true;
            case R.id.contact_export_json:
                Toast.makeText(this, "Exporting contacts to JSON...", Toast.LENGTH_SHORT).show();
                new SaveContact(R.id.contact_export_json).execute();
                return true;
            case R.id.contact_help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.contact_settings:
              // Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();

                Intent newIntent = new Intent(this,ExportedItemsActivity.class);
//                EditText edittext = (EditText)findViewById(R.id.editText);
//                String message = edittext.getText().toString();
//                newIntent.putExtra(EXTRA_MESSAGE,message);
                startActivity(newIntent);

//                ExportsFragment exFragment = new ExportsFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.singleView, exFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
                //getSupportFragmentManager().beginTransaction().add(R.id.ExportList, exFragment).commit();
                return true;
            case R.id.contact_info:
                InfoFragment info = new InfoFragment();
                info.show(getSupportFragmentManager(), "ShowInfo");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onFragmentInteraction(QuickViewDetails quick) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
        ContactDetails details = (ContactDetails)getSupportFragmentManager().findFragmentById(R.id.contactDetails_fragment);

        if(details != null){
            details.updateArticleView(quick);
        }else{
            ContactDetails newDetails = new ContactDetails();
            Bundle args = new Bundle();
            args.putSerializable(ContactDetails.ARG, quick);
            newDetails.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.singleView, newDetails);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

//    @Override
//    public void onListFragmentInteraction(DummyContent.DummyItem item) {
//
//    }


    //    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
//                // getLoaderManager().initLoader(ContactsList.QUERY_ID, null, this);
//                //Util.saveToFile("self.csv","hello,hi,welcome",getBaseContext());
//                // Toast.makeText(this, "Write Permission Granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    private class SaveContact extends AsyncTask<Void, Integer, String> {
        // private ProgressDialog progressBar;
     private int _menu;

     public SaveContact(int menu){
         _menu = menu;
     }
        @Override
        protected void onPostExecute(String  result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            // progressBar.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
//            progressBar = new ProgressDialog(MainActivity.this);
//            progressBar.setCancelable(false);
//            progressBar.setMessage("Fetching Contacts....");
//            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressBar.setIndeterminate(true);
//            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... obj) {

            LoadContacts process =  new LoadContacts();
            List<ContactObject> items =  process.GetContacts(getBaseContext(),"");
            switch(_menu){

                case R.id.contact_export_csv:
                    String csv = Util.getCSV(items);
                    Util.saveToFile("ContactExport.csv",csv,getBaseContext());
                    break;
                case R.id.contact_export_xml:
                    String xml = Util.getXml(items);
                    Util.saveToFile("ContactExport.xml",xml,getBaseContext());
                    break;
                case R.id.contact_export_json:
                    String json = Util.getJSON(items);
                    Util.saveToFile("ContactExport.json",json,getBaseContext());
                    break;

            }
//           0 for (int i = 0; i < count; i++) {
//                //  dbHelper.addContact(contactObjects[i]);
//
//
//
//                publishProgress((int) ((i / (float) count) * 100));
//                totalSize++;
//                // Escape early if cancel() is called
//                if (isCancelled()) break;
//            }

            String returnString = String.format("Processed. Total Number of Contacts: %1s",items.size());
            // progressBar.setMessage(returnString);
            return returnString;
        }
    }

}
