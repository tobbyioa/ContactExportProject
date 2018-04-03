package com.webaholics.olufemiisola.contactexport;


import android.os.Environment;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webaholics.olufemiisola.contactexport.ExportedItemsAdapter.ViewHolder.ClickListener;

import java.util.List;

public class ExportedItemsActivity extends AppCompatActivity implements ClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = ExportedItemsActivity.class.getSimpleName();

    private RecyclerView mrc_view;
    private ExportedItemsAdapter rc_adapter;
    private RecyclerView.LayoutManager rc_lc_manager;
  //  private ExportedItemsAdapter adapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    FloatingActionButton primaryFab;
    FloatingActionButton shareFab;
    FloatingActionButton deleteFab;
    FloatingActionButton deleteSweepFab;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exported_items);
        mrc_view =  (RecyclerView) findViewById(R.id.recyclerView);
//        mrc_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//
//
//            }
//        });



        //Instantiate and set adapter here
        String WebaholicsFolder = "ContactExport";
        List<ExportedItem> entries = Util.getExportedItems(Environment.getExternalStorageDirectory().getPath()+"/"+WebaholicsFolder);
        rc_adapter = new ExportedItemsAdapter(entries, this, this);
        mrc_view.setAdapter(rc_adapter);

        mrc_view.setHasFixedSize(true);
        mrc_view.setItemAnimator(new DefaultItemAnimator());
        rc_lc_manager = new LinearLayoutManager(this);
        mrc_view.setLayoutManager(rc_lc_manager);

        primaryFab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        shareFab = (FloatingActionButton)findViewById(R.id.floatingActionButtonShare);
        deleteFab = (FloatingActionButton)findViewById(R.id.floatingActionButtonDelete);
        deleteSweepFab = (FloatingActionButton)findViewById(R.id.floatingActionButtonDeleteSweep);
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
            if(isFABOpen){
                closeFABMenu();
            }
            actionMode.finish();
        } else {

            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
            if(!isFABOpen){
                showFABMenu();
            }
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    // TODO: actually remove items
                    Log.d(TAG, "menu_remove");
                    mode.finish();
                    return true;

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
}
