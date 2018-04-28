package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.R;

import java.util.ArrayList;


public class PinListAdapter extends RecyclerView.Adapter<PinListAdapter.ViewHolder> {

    private static final String TAG = PinListAdapter.class.getSimpleName();
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        /*Get a pin from position.*/
        final Pin pin = pinList.get(position);

        /*Set the pin title view.*/
        holder.pinTitle.setText(pin.getTitle());
            /*Set the pin image view.*/
            if (pin.getImage()!=null) {
                holder.pinImage.setVisibility(View.VISIBLE);
                holder.pinVideo.setVisibility(View.INVISIBLE);
                holder.playBtn.setVisibility(View.INVISIBLE);
                holder.pinImage.setImageURI(pin.getImage());
                holder.pinImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Notify the main activity of the board_item image clicked.*/
                        listener.onPinListAdapterInteraction(pin);
                    }
                });
            /*Set the pin video view.*/
            } else if (pin.getVideo()!=null) {
                holder.pinImage.setVisibility(View.INVISIBLE);
                holder.pinVideo.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.VISIBLE);
                holder.pinVideo.setVideoURI(pin.getVideo());
                /*https://stackoverflow.com/questions/17079593/how-to-set-the-preview-image-in-videoview-before-playing*/
                holder.pinVideo.pause();
                holder.pinVideo.seekTo(100); // 100 milliseconds (0.1 s) into the clip.
                holder.pinVideoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Notify the main activity of the board_item image clicked.*/
                        listener.onPinListAdapterInteraction(pin);
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        if(pinList!=null){
           return pinList.size();
        }else{
           return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView pinTitle;
        public final ImageView pinImage;
        public final VideoView pinVideo;
        public final ImageView pinVideoView;
        public final ImageView playBtn;

        public ViewHolder(View view) {
            super(view);
            pinTitle = (TextView) view.findViewById(R.id.pinTitle);
            pinImage = (ImageView) view.findViewById(R.id.pinImage);
            pinVideo = (VideoView) view.findViewById(R.id.pinVideo);
            pinVideoView = (ImageView) view.findViewById(R.id.pinVideoView);
            playBtn = (ImageView) view.findViewById(R.id.playBtn);
        }
    }
}
