package com.webaholics.olufemiisola.contactexport;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.webaholics.olufemiisola.contactexport.ExportedItemsAdapter.ViewHolder.ClickListener;

import java.io.File;
import java.util.ArrayList;
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
    private  List<ExportedItem> entries;
    List<ExportedItem>  selectedItems;
    List<String>  mimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exported_items);
        mrc_view =  (RecyclerView) findViewById(R.id.recyclerView);

        //Instantiate and set adapter here
        String WebaholicsFolder = "ContactExport";
        entries = Util.getExportedItems(Environment.getExternalStorageDirectory().getPath()+"/"+WebaholicsFolder);
        rc_adapter = new ExportedItemsAdapter(entries, this, this);
        this.selectedItems = new ArrayList<ExportedItem>();
        this.mimes = new ArrayList<String>();
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

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Sharing....", Toast.LENGTH_SHORT).show();
                if(selectedItems.isEmpty()){
                    Toast.makeText(getBaseContext(), "No File Selected for export", Toast.LENGTH_SHORT).show();
                }else {
                    ArrayList<Uri> imageUris = new ArrayList<Uri>();
                    for(int i = 0;i < selectedItems.size();i++)
                    {
                        Uri imageUri = Uri.fromFile(new File(selectedItems.get(i).get_path()));
                        imageUris.add(imageUri); // Add your image URIs here
                    }
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    shareIntent.setType(selectedItems.get(0).getMimeType());
                    startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                    //Toast.makeText(getBaseContext(), "CSV Export Complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        hideFloatingActionButton(shareFab);
    }
    private void showFABMenu(){

        isFABOpen=true;
        if(FloatingActionButtonHideStatus(shareFab)) {
            4
            zshareFab.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
            a7qqqqqqqqqq7212        }
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
            selectedItems.clear();
            mimes.clear();
            actionMode.finish();
        } else {

            selectedItems.clear();
            mimes.clear();
            for(int i = 0;i < rc_adapter.getSelectedItems().size();i++){
                ExportedItem item = entries.get(position);
                selectedItems.add(item);
                mimes.add(item.getMimeType());
            }

            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
            if(!isFABOpen){
                showFABMenu();
            }
            if(selectedItems.size() < 1){
                hideFloatingActionButton(shareFab);
            }else if(selectedItems.size() == 1){
                showFloatingActionButton(shareFab);
            }else{
                hideFloatingActionButton(shareFab);
            }

            if(Util.isListContainMethod(mimes,mimes.get(0))){
                showFloatingActionButton(shareFab);
            }else{
                hideFloatingActionButton(shareFab);
            }

            actionMode.invalidate();
        }
    }

    private void hideFloatingActionButton(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    private void showFloatingActionButton(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }

    private boolean FloatingActionButtonHideStatus (FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

       return  behavior.isAutoHideEnabled();

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
