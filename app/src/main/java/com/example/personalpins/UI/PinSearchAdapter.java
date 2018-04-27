package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PinSearchAdapter extends RecyclerView.Adapter<PinSearchAdapter.ViewHolder> {

    private final ArrayList<String> searchQuery;
    private final InteractionListener listener;

    public PinSearchAdapter(ArrayList<String> query, InteractionListener listener) {
        searchQuery = query;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.pinTitle.setText(searchQuery.get(position));

        Picasso.with(holder.pinImage.getContext())
                .load(R.drawable.ic_board_image_default)
                .fit()
                .centerCrop()
                .into(holder.pinImage);

    }

    @Override
    public int getItemCount() {
        return searchQuery.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView pinTitle;
        public final ImageView pinImage;

        public ViewHolder(View view) {
            super(view);
            pinTitle = (TextView) view.findViewById(R.id.pinTitle);
            pinImage = (ImageView) view.findViewById(R.id.pinImage);
        }
    }
}
