package com.webaholics.olufemiisola.contactexport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A simple {@link } subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //,AdapterView.OnItemClickListener
    private OnFragmentInteractionListener mListener;

    long mContactId;
    String mContactKey;
    Uri mContactUri;
    MySimpleCursorAdapter mCursorAdapter;
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;
    private static int QUERY_ID = 0;
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = '1'" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? AND "+ContactsContract.Contacts.HAS_PHONE_NUMBER +" = '1'";
    // Defines a variable for the search string
    private String mSearchString = "";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION ={
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.HONEYCOMB) ?
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI:
                    ContactsContract.Contacts._ID,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME

    };

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS =
            {Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY,ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
    private  final static  int[] TO_IDS = {R.id.contactName,R.id.contactId,R.id.contactLookupKey,R.id.quickContactBadge};

    String sortOrder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?ContactsContract.Contacts.DISPLAY_NAME_PRIMARY+ " ASC" :ContactsContract.Contacts.DISPLAY_NAME + " ASC";
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    int mThumbnailColumn;
    String mThumbnailUri;
    // The index of the _ID column in the Cursor
    int mIdColumn;
    int mLookupKeyColumn;

    private String mSearchTerm; // Stores the current search query term
    // Whether or not the search query has changed since the last time the loader was refreshed
    private boolean mSearchQueryChanged;
    // Whether or not this is a search result view of this fragment, only used on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search from honeycomb onward
    private boolean mIsSearchResultView = false;

    public ContactsList() {
        // Required empty public constructor
    }

    /**
     * In platform versions prior to Android 3.0, the ActionBar and SearchView are not supported,
     * and the UI gets the search string from an EditText. However, the fragment doesn't allow
     * another search when search results are already showing. This would confuse the user, because
     * the resulting search would re-query the Contacts Provider instead of searching the listed
     * results. This method sets the search query and also a boolean that tracks if this Fragment
     * should be displayed as a search result view or not.
     *
     * @param query The contacts search query.
     */
    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mIsSearchResultView = false;
        } else {
            mSearchTerm = query;
            mIsSearchResultView = true;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.contactDetails_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mContactList =(ListView) getActivity().findViewById(R.id.contactList);

        // Gets a CursorAdapter
        mCursorAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.contact_list_row,null,FROM_COLUMNS, TO_IDS,0);

        // mCursorAdapter.setViewBinder(new CustomViewBinder());

        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previous search term and previously selected search
            // result.
            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
        }

        // Sets the adapter for the ListView
        setListAdapter(mCursorAdapter);
        //setOnItemClickListener(this);
        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
            getLoaderManager().initLoader(ContactsList.QUERY_ID, null, this);
       // }
        //getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.INVISIBLE);
        FloatingActionButton primaryFab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getLoaderManager().initLoader(ContactsList.QUERY_ID, null, this);
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.contacts_list, container, false);
//    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.search_contacts, menu);
        MenuItem myActionMenuItem = menu.findItem( R.id.contactSearch);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        // Inflate the menu items
//        inflater.inflate(R.menu.search_contacts, menu);
//        // Locate the search item
//        MenuItem searchItem = menu.findItem(R.id.contactSearch);
//
//        if (mIsSearchResultView) {
//            searchItem.setVisible(false);
//        }
//
//        if (Util.hasHoneycomb()) {
//
//            // Retrieves the system search manager service
//            final SearchManager searchManager =
//                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//
//            // Retrieves the SearchView from the search menu item
//            final SearchView searchView = (SearchView) searchItem.getActionView();
//
//            // Assign searchable info to SearchView
//            searchView.setSearchableInfo(
//                    searchManager.getSearchableInfo(getActivity().getComponentName()));
//
//            // Set listeners for SearchView
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//                @Override
//                public boolean onQueryTextSubmit(String queryText) {
//                    // Nothing needs to happen when the user submits the search string
//                    return true;
//                }
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    // Called when the action bar search text has changed.  Updates
//                    // the search filter, and restarts the loader to do a new query
//                    // using the new search string.
//                    String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
//
//                    // Don't do anything if the filter is empty
//                    if (mSearchTerm == null && newFilter == null) {
//                        return true;
//                    }
//
//                    // Don't do anything if the new filter is the same as the current filter
//                    if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
//                        return true;
//                    }
//
//                    // Updates current filter to new filter
//                    mSearchTerm = newFilter;
//
//                    // Restarts the loader. This triggers onCreateLoader(), which builds the
//                    // necessary content Uri from mSearchTerm.
//                    mSearchQueryChanged = true;
//                    getLoaderManager().restartLoader(ContactsList.QUERY_ID, null, ContactsList.this);
//                    return true;
//                }
//            });
//
//            if (Util.hasICS()) {
//                // This listener added in ICS
//                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//                    @Override
//                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
//                        // Nothing to do when the action item is expanded
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                        // When the user collapses the SearchView the current search string is
//                        // cleared and the loader restarted.
//                        if (!TextUtils.isEmpty(mSearchTerm)) {
//                           // onSelectionCleared();
//                        }
//                        mSearchTerm = null;
//                        getLoaderManager().restartLoader(
//                                ContactsList.QUERY_ID, null, ContactsList.this);
//                        return true;
//                    }
//                });
//            }
//
//            if (mSearchTerm != null) {
//                // If search term is already set here then this fragment is
//                // being restored from a saved state and the search menu item
//                // needs to be expanded and populated again.
//
//                // Stores the search term (as it will be wiped out by
//                // onQueryTextChange() when the menu item is expanded).
//                final String savedSearchTerm = mSearchTerm;
//
//                // Expands the search menu item
//                if (Util.hasICS()) {
//                    searchItem.expandActionView();
//                }
//
//                // Sets the SearchView to the previous search string
//                searchView.setQuery(savedSearchTerm, false);
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Sends a request to the People app to display the create contact screen
//            case R.id.menu_add_contact:
//                final Intent intent = new Intent(Intent.ACTION_INSERT, Contacts.CONTENT_URI);
//                startActivity(intent);
//                break;
            // For platforms earlier than Android 3.0, triggers the search activity
            case R.id.contactSearch:
                if (!Util.hasHoneycomb()) {
                    getActivity().onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!TextUtils.isEmpty(mSearchTerm)) {
            // Saves the current search string
            outState.putString(SearchManager.QUERY, mSearchTerm);

            // Saves the currently selected contact
            // outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView().getCheckedItemPosition());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

// If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsList.QUERY_ID) {
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
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
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
        getLoaderManager().restartLoader(ContactsList.QUERY_ID, null, ContactsList.this);
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Cursor cursor  = ((SimpleCursorAdapter)adapterView.getAdapter()).getCursor();
//        cursor.moveToPosition(i);
//
//        mContactId = cursor.getLong(CONTACT_ID_INDEX);
//        mContactKey = cursor.getString(CONTACT_ID_INDEX);
//
//        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId,mContactKey);
//
//
//
//        // mListener.onFragmentInteraction(mContactUri);
//
//        // Set the item as checked to be highlighted when in two-pane layout
//        getListView().setItemChecked(i, true);
//    }

    // @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//
//        Cursor cursor  = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
//        cursor.moveToPosition(position);
//        mIdColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//
//        mLookupKeyColumn = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
//        // Gets a content URI for the contact
//        mContactUri =
//                ContactsContract.Contacts.getLookupUri(
//                        cursor.getLong(mIdColumn),
//                        cursor.getString(mLookupKeyColumn)
//                );
//
////        mContactId = cursor.getLong(mIdColumn);
////        mContactKey = cursor.getString(mIdColumn);
////        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId,mContactKey);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mThumbnailColumn =
//                    cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
//            // Otherwise, sets the thumbnail column to the _ID column
//        } else {
//            mThumbnailColumn = mIdColumn;
//        }
//
//        mThumbnailUri = cursor.getString(mThumbnailColumn);
//
//        QuickViewDetails quickViewDetails =  new QuickViewDetails();
//        quickViewDetails.set_contactUri(mContactUri);
//        quickViewDetails.set_contactThumbNailUri(mThumbnailUri);
//
//        mListener.onFragmentInteraction(quickViewDetails);
//
//        // Set the item as checked to be highlighted when in two-pane layout
//        getListView().setItemChecked(position, true);
//
//       // super.onListItemClick(l, v, position, id);
//        // Notify the parent activity of selected item
//     //   mListener.onFragmentInteraction(position);
//
//        // Set the item as checked to be highlighted when in two-pane layout
//      //  getListView().setItemChecked(position, true);
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(QuickViewDetails quick);
    }


//    private void updateArticleView( View view, QuickViewDetails quickViewDetails,String name) {
//
//        TextView txt=  (TextView) view.findViewById(R.id.contactName);
//
//        txt.setText(name);
//
//        QuickContactBadge mBadge = (QuickContactBadge) view.findViewById(R.id.quickContactBadge);
//        mBadge.assignContactUri(quickViewDetails.get_contactUri());
//
//        Bitmap mThumbnail =
//                loadContactPhotoThumbnail(quickViewDetails.get_contactThumbNailUri());
//    /*
//     * Sets the image in the QuickContactBadge
//     * QuickContactBadge inherits from ImageView, so
//     */
//        mBadge.setImageBitmap(mThumbnail);
////        article.setText(Ipsum.Articles[position]);
////        mCurrentPosition = position;
//    }



    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder{

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex)
        {
            Long a = (cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
            String b = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)));
            String c = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
            String d  = "";
            TextView txt=  (TextView) view.findViewById(R.id.contactName);

            txt.setText(c);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//               d = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
//                // Otherwise, sets the thumbnail column to the _ID column
//            } else {
//                d = a.toString();
//            }
//
//            d = d == null ? a.toString(): d;
//
//            Uri mContactUri =
//                        ContactsContract.Contacts.getLookupUri(
//                                a,
//                                b
//                        );
//
//            QuickViewDetails quickViewDetails =  new QuickViewDetails();
//               quickViewDetails.set_contactUri(mContactUri);
//                quickViewDetails.set_contactThumbNailUri(mThumbnailUri);
//
//            updateArticleView(view,quickViewDetails,c);
//            if (columnIndex == cursor.getColumnIndex(ClubCP.KEY_IS_STAR)) {
//                // If the column is IS_STAR then we use custom view.
//                int is_star = cursor.getInt(columnIndex);
//                if (is_star != 1) {
//                    // set the visibility of the view to GONE
//                    view.setVisibility(View.GONE);
//                }
//                return true;
//            }

//           if (columnIndex == cursor.getColumnIndex(ContactsContract.Contacts._ID)) {
//                mIdColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//
//                mLookupKeyColumn = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
//                // Gets a content URI for the contact
//                mContactUri =
//                        ContactsContract.Contacts.getLookupUri(
//                                cursor.getLong(mIdColumn),
//                                cursor.getString(mLookupKeyColumn)
//                        );
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    mThumbnailColumn =
//                            cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
//                    // Otherwise, sets the thumbnail column to the _ID column
//                } else {
//                    mThumbnailColumn = mIdColumn;
//                }
//
//                mThumbnailUri = cursor.getString(mThumbnailColumn);
//
//
//                QuickViewDetails quickViewDetails =  new QuickViewDetails();
//                quickViewDetails.set_contactUri(mContactUri);
//                quickViewDetails.set_contactThumbNailUri(mThumbnailUri);
//
//                updateArticleView(quickViewDetails);
//
            return  true;
//
//            }
            // return false;
        }
    }

}
