package com.cawapp.androidtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cawapp.androidtest.R;
import com.cawapp.androidtest.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Item> itemsList;
    private boolean activeSelection = false;
    private ItemClickListener clickListener;


    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public MasterAdapter(Context context, ArrayList<Item> newsList, ItemClickListener listener) {
        this.context = context;
        this.itemsList = newsList;
        this.clickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_item, parent, false);

        return new ViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.nameView.setText(item.getName());
        Picasso .with(context)
                .load(item.getImage())
                .into(holder.imageView);

        if(activeSelection) {
            if (item.isSelected()) {
                holder.bg.setSelected(true);
            } else {
                holder.bg.setSelected(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView nameView;
        private ImageView imageView;
        private LinearLayout bg;
        private ItemClickListener mListener;

        public ViewHolder(View view, ItemClickListener listener) {
            super(view);

            this.mListener = listener;

            this.bg = view.findViewById(R.id.bg);
            this.nameView = view.findViewById(R.id.name);
            this.imageView = view.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(mListener != null) {
                mListener.onClick(view, getLayoutPosition());
            }
        }
    }

    /**
     * Active the selection of a cell
     * @param state true / false
     */
    public void activeSelection(boolean state){
        activeSelection = state;
    }


}
