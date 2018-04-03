package com.webaholics.olufemiisola.contactexport;

import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactAdapter.ViewHolder.ClickListener,LoaderManager.LoaderCallbacks<Cursor> {


    @SuppressWarnings("unused")
    private static final String TAG = ContactActivity.class.getSimpleName();
    private boolean isSearchResultView = false;
    private RecyclerView mrc_view;
    private ContactAdapter rc_adapter;
    private RecyclerView.LayoutManager rc_lc_manager;
    private ContactActivity.ActionModeCallback actionModeCallback = new ContactActivity.ActionModeCallback();
    private ActionMode actionMode;
    private static final int PERMISSIONS_ALL = 1;
    String[] projection = {
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

    String sortOrder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?ContactsContract.Contacts.DISPLAY_NAME_PRIMARY+ " ASC" :ContactsContract.Contacts.DISPLAY_NAME + " ASC";

    private String mSearchTerm; // Stores the current search query term
    // Whether or not the search query has changed since the last time the loader was refreshed
    private boolean mSearchQueryChanged;
    // Whether or not this is a search result view of this fragment, only used on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search from honeycomb onward
    private boolean mIsSearchResultView = false;
    private  int QUERY_ID = 0;
    Cursor data;
    FloatingActionButton primaryFab;
    FloatingActionButton xmlFab;
    FloatingActionButton jsonFab;
    FloatingActionButton xlFab;
    FloatingActionButton csvFab;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactslayout);
        if(Util.checkAndRequestPermissions(this,PERMISSIONS_ALL)) {
            mrc_view = (RecyclerView) findViewById(R.id.contactView);
            LoadContacts process = new LoadContacts();
            List<ContactObject> items = process.GetAllContacts(getBaseContext());
            rc_adapter = new ContactAdapter(data, this, this);
            mrc_view.setAdapter(rc_adapter);
            getLoaderManager().initLoader(QUERY_ID, null, this);
            mrc_view.setHasFixedSize(true);
            mrc_view.setItemAnimator(new DefaultItemAnimator());
            rc_lc_manager = new LinearLayoutManager(this);
            mrc_view.setLayoutManager(rc_lc_manager);
            primaryFab = (FloatingActionButton) findViewById(R.id.floatingActionButtonCall);
            xmlFab = (FloatingActionButton) findViewById(R.id.floatingActionButtonXML);
            csvFab = (FloatingActionButton) findViewById(R.id.floatingActionButtonCSV);
            xlFab = (FloatingActionButton) findViewById(R.id.floatingActionButtonXL);
            jsonFab = (FloatingActionButton) findViewById(R.id.floatingActionButtonJSON);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                xmlFab.setEnabled(false);
                csvFab.setEnabled(false);
                xlFab.setEnabled(false);
                jsonFab.setEnabled(false);
            }

            primaryFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isFABOpen){
                        showFABMenu();
                    }else{
                        closeFABMenu();
                    }
                }
            });
            xmlFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "XML Export Starting....", Toast.LENGTH_SHORT).show();
                    new ContactActivity.SaveContact(R.id.contact_export_xml).execute();
                    //Toast.makeText(getBaseContext(), "XML Export Complete", Toast.LENGTH_SHORT).show();
                }
            });
            csvFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "CSV Export Starting....", Toast.LENGTH_SHORT).show();
                    new ContactActivity.SaveContact(R.id.contact_export_csv).execute();
                    //Toast.makeText(getBaseContext(), "CSV Export Complete", Toast.LENGTH_SHORT).show();
                }
            });
            jsonFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "JSON Export Starting....", Toast.LENGTH_SHORT).show();
                    new ContactActivity.SaveContact(R.id.contact_export_json).execute();
                    //Toast.makeText(getBaseContext(), "JSON Export Complete", Toast.LENGTH_SHORT).show();
                }
            });
            xlFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "Excel export selected", Toast.LENGTH_SHORT).show();
                }
            });

            if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

                // Fetch query from intent and notify the fragment that it should display search
                // results instead of all contacts.
                String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);

                isSearchResultView = true;
                setSearchQuery(searchQuery);
                // Set special title for search results
                String title = getString(R.string.contacts_list_search_results_title, searchQuery);
                setTitle(title);
            }

        }else{

            finish();
            startActivity(getIntent());
        }
    }

    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mIsSearchResultView = false;
        } else {
            mSearchTerm = query;
            mIsSearchResultView = true;
        }
    }

    private void showFABMenu(){
        isFABOpen=true;
        csvFab.animate().translationY(-getResources().getDimension(R.dimen.standard_260));
        jsonFab.animate().translationY(-getResources().getDimension(R.dimen.standard_195));
        xmlFab.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
        xlFab.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        csvFab.animate().translationY(0);
        jsonFab.animate().translationY(0);
        xmlFab.animate().translationY(0);
        xlFab.animate().translationY(0);
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }


    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        rc_adapter.toggleSelection(position);
        int count = rc_adapter.getSelectedItemCount();

        if (count == 0) {
//                if(isFABOpen){
//                    closeFABMenu();
//                }
            actionMode.finish();
        } else {

            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
//            if(!isFABOpen){
//                showFABMenu();
//            }
            actionMode.invalidate();
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

// If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == QUERY_ID) {
            Uri contentUri;

            // There are two types of searches, one which displays all contacts and
            // one which filters contacts by a search query. If mSearchTerm is set
            // then a search query has been entered and the latter should be used.

            if (mSearchTerm == null) {
                // Since there's no search string, use the content URI that searches the entire
                // Contacts table
                //  contentUri = ContactsQuery.CONTENT_URI;
                mSearchString = "";
            } else {
                // Since there's a search string, use the special content Uri that searches the
                // Contacts table. The URI consists of a base Uri and the search string.
                //contentUri =Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
                mSearchString = mSearchTerm;
            }
        }

        mSelectionArgs[0] = "%" + mSearchString + "%";

        // Starts the query
        return new CursorLoader(
               this,
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                SELECTION,
                mSelectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        rc_adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        rc_adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQueryEvent event) {
        String query = event.getQuery();
        mSearchTerm = query;
        getLoaderManager().restartLoader(QUERY_ID, null, this);
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

            MenuItem stMenu = menu.findItem( R.id.contact_settings);
            if(stMenu !=null){
                stMenu.setEnabled(false);
            }
        }
        menu.findItem( R.id.contact_export_vcf).setVisible(false);

        menu.findItem( R.id.contact_export_xml).setVisible(false);

         menu.findItem( R.id.contact_export_csv).setVisible(false);

        menu.findItem( R.id.contact_export_json).setVisible(false);

        menu.findItem( R.id.contact_export_xl).setVisible(false);

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
            case R.id.contact_help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.contact_settings:
                // Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(this,ExportedItemsActivity.class);
                startActivity(newIntent);
                return true;
            case R.id.contact_info:
                InfoFragment info = new InfoFragment();
                info.show(getSupportFragmentManager(), "ShowInfo");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ContactActivity.ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.menu_remove:
//                    // TODO: actually remove items
//                    Log.d(TAG, "menu_remove");
//                    mode.finish();
//                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            rc_adapter.clearSelection();
            actionMode = null;
        }
    }

    private class SaveContact extends AsyncTask<Void, Integer, String> {
        private int _menu;

        public SaveContact(int menu){
            _menu = menu;
        }
        @Override
        protected void onPostExecute(String  result) {

            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
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

            String returnString = String.format("Processed. Total Number of Contacts: %1s",items.size());

            return returnString;
        }
    }

}
