package com.webaholics.olufemiisola.contactexport;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olufemi Isola on 25/02/18.
 */
//extends SelectableAdapter<Adapter.ViewHolder>
//extends RecyclerView.Adapter<ExportedItemsAdapter.ViewHolder>
public class ExportedItemsAdapter extends SelectableAdapter<ExportedItemsAdapter.ViewHolder> {


    private List<ExportedItem> data;
    private Context context;
    private int selectedItem = 0;

    private SparseBooleanArray selectedItems;
    private ViewHolder.ClickListener clickListener;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        @SuppressWarnings("unused")
        private  final String TAG = ViewHolder.class.getSimpleName();
        public ImageView fileType;
        public ImageView exportIcon;
        public TextView text;
        public ImageButton share;
        View selectedOverlay;
        private ClickListener listener;


        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            fileType = (ImageView)itemView.findViewById(R.id.rc_fileTypeImage);
            exportIcon = (ImageView)itemView.findViewById(R.id.rc_imageView);
            text = (TextView)itemView.findViewById(R.id.rc_fileName);
            share = (ImageButton)itemView.findViewById(R.id.rc_fileShareBtn);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

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

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
//        super.onBindViewHolder(holder, position, payloads);
//        holder.itemView.setSelected(selectedItem == position);
//    }

//    @Override
//    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        // Handle key up and key down and attempt to move selection
//        recyclerView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
//
//                // Return false if scrolled to the bounds and allow focus to move off the list
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//                        return tryMoveSelection(lm, 1);
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//                        return tryMoveSelection(lm, -1);
//                    }
//                }
//
//                return false;
//            }
//        });
//    }
//
//    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
//        int tryFocusItem = selectedItem + direction;
//
//        // If still within valid bounds, move the selection, notify to redraw, and scroll
//        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
//            notifyItemChanged(selectedItem);
//            selectedItem = tryFocusItem;
//            notifyItemChanged(selectedItem);
//            lm.scrollToPosition(selectedItem);
//            return true;
//        }
//
//        return false;
//    }
//
//    public void setData(List<ExportedItem> data) {
//
//        int curSize = getItemCount();
//
//        List<ExportedItem> newItems = data;
//
//
//        data.addAll(newItems);
//
//        this.notifyItemRangeInserted(curSize, newItems.size());
//
//    }
//    public void removeData(ExportedItem  item) {
//
//        data.remove(item);
//    }
//    public void removeData(int  itemPosition) {
//
//        data.remove(itemPosition);
//    }

    public ExportedItemsAdapter(List<ExportedItem> data, Context context, ViewHolder.ClickListener clickListener) {
        this.data = data;
        this.context = context;
        this.clickListener = clickListener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public ExportedItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View exportedItem = inflater.inflate(R.layout.exported_item,parent,false);
        ViewHolder holder = new ViewHolder(exportedItem,clickListener);
        return holder;
    }



    @Override
    public void onBindViewHolder(ExportedItemsAdapter.ViewHolder holder, int position) {
        ExportedItem item = data.get(position);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.fileType.setImageDrawable(context.getResources().getDrawable(item.get_drawableIcon(), context.getTheme()));
        }else{
            holder.fileType.setImageDrawable(context.getResources().getDrawable(item.get_drawableIcon()));
        }
        holder.text.setText(item.get_name());
        holder.exportIcon.setVisibility(View.VISIBLE);

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
        return data.size();
    }

//    public ExportedItem getItem(int position) {
//        return data.get(position);
//    }
//
//    public void toggleSelection(int position){
//
//        if(selectedItems.get(position,false)){
//            selectedItems.delete(position);
//        }else{
//            selectedItems.put(position,true);
//        }
//        notifyItemChanged(position);
//    }
//    public void clearSelections(){
//        selectedItems.clear();
//        notifyDataSetChanged();
//    }
//
//    public int getSelectedItemCount(){
//        return selectedItems.size();
//    }
//    public List<Integer> getSelectedItems() {
//        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
//        for (int i = 0; i < selectedItems.size(); i++) {
//            items.add(selectedItems.keyAt(i));
//        }
//        return items;
//    }

}
