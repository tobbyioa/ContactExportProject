package com.webaholics.olufemiisola.contactexport;

//import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link}
 * interface.
 */
// OnListFragmentInteractionListener
public class ExportsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ExportItem>>  {

    //

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
   //private OnListFragmentInteractionListener mListener;
   ExportItemAdapter mAdapter;
   FloatingActionButton primaryFab;
    FloatingActionButton shareFab;
    FloatingActionButton deleteFab;
    FloatingActionButton deleteSweepFab;
    //ListView lv;
   boolean isFABOpen = false;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExportsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ExportsFragment newInstance(int columnCount) {
        ExportsFragment fragment = new ExportsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    private void showFABMenu(){
        isFABOpen=true;
        shareFab.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        deleteFab.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
        deleteSweepFab.animate().translationY(-getResources().getDimension(R.dimen.standard_195));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        shareFab.animate().translationY(0);
        deleteFab.animate().translationY(0);
        deleteSweepFab.animate().translationY(0);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No Data Here");
       // ((TextView)getListView().getEmptyView()).setText(getString(R.string.empty_message));
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new ExportItemAdapter(getActivity(),R.layout.fragment_exportitem_list);
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
       // getActivity().findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
       // getView();
        primaryFab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        shareFab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButtonShare);
        deleteFab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButtonDelete);
        deleteSweepFab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButtonDeleteSweep);
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
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        //return super.onCreateView(inflater, container, savedInstanceState);
//        View rootView = inflater.inflate(R.layout.fragment_exportitem_list, container, false);

//
//        return rootView;
//    }
    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        primaryFab = (FloatingActionButton) getView().findViewById(R.id.floatingActionButton);
//
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
//    }

    @Override
    public Loader<List<ExportItem>> onCreateLoader(int id, Bundle args) {

        return new ExportListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ExportItem>> loader, List<ExportItem> data) {
        mAdapter.setData(data);
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ExportItem>> loader) {
        mAdapter.setData(null);
    }

    private static class ExportListLoader extends AsyncTaskLoader<List<ExportItem>> {

        List<ExportItem> mModels;

        public ExportListLoader(Context context) {
            super(context);
        }
        @Override
        public List<ExportItem> loadInBackground() {
            System.out.println("DataListLoader.loadInBackground");

            // You should perform the heavy task of getting data from
            // Internet or database or other source
            // Here, we are generating some Sample data
            // Create corresponding array of entries and load with data.
            String WebaholicsFolder = "ContactExport";
            List<ExportItem> entries = Util.getMyFiles(Environment.getExternalStorageDirectory().getPath()+"/"+WebaholicsFolder,this.getContext());
            return entries;
        }
        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<ExportItem> listOfData) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (listOfData != null) {
                    onReleaseResources(listOfData);
                }
            }
            List<ExportItem> oldApps = listOfData;
            mModels = listOfData;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(listOfData);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mModels != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mModels);
            }


            if (takeContentChanged() || mModels == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<ExportItem> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mModels != null) {
                onReleaseResources(mModels);
                mModels = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<ExportItem> apps) {}

    }



    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_exportitem_list, container, false);
//
//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new MyExportItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
//        }
//        return view;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onListFragmentInteraction(DummyItem item);
//    }
}
