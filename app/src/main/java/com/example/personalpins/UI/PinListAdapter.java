package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PinListAdapter extends RecyclerView.Adapter<PinListAdapter.ViewHolder> {

    private ArrayList<Pin> pinList;
    InteractionListener listener;

    public PinListAdapter(ArrayList<Pin> pinList, InteractionListener listener) {
        this.pinList = pinList;
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
        holder.pinTitle.setText(pinList.get(position).getTitle());

        final Pin pin = pinList.get(position);

        /*Set the adapter holder view with the image boardUri.*/
        Picasso.with(holder.pinImage.getContext())
                .load(pinList.get(position).getImageUri())
                .into(holder.pinImage);

        /*Notify the main activity of the board_item image clicked.*/
        holder.pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPinListAdapterInteraction(pin);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(pinList!=null){
           return pinList.size();
        }else{
           return 1;
        }
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
