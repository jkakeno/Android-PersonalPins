package com.example.personalpins.UI;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Comment;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.Model.Tag;
import com.example.personalpins.R;

import java.util.ArrayList;

public class PinDetailFragment extends Fragment{

    private static final String TAG = PinDetailFragment.class.getSimpleName();
    private static final String ARG = "pin";
    InteractionListener listener;
    Pin pin;
    ArrayList<Tag> tagList;
    ArrayList<Comment> commentList;
    RecyclerView tagRecyclerView;
    RecyclerView commentRecyclerView;
    TagListAdapter tagListAdapter;
    CommentListAdapter commentListAdapter;

    View view;
    ImageView pinImage;
    TextView noTags;
    TextView noComments;
    TextView pinTitle;
    VideoView pinVideo;
    ImageView playBtn;
    ImageView pinVideoView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinDetailFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static PinDetailFragment newInstance(Pin pin) {
        PinDetailFragment fragment = new PinDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG, pin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        if (getArguments() != null) {
            /*Get the data from static field ARG.*/
            pin = getArguments().getParcelable(ARG);
        }
        if(pin!=null) {
            if (pin.getTagList() != null) {
                tagList = pin.getTagList();
            }
            if (pin.getCommentList() != null) {
                commentList = pin.getCommentList();
            }
        }

        /*Create menu items for this fragment.*/
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        /*Inflate all the views.*/
        view = inflater.inflate(R.layout.pin_detail_fragment, container, false);
        pinTitle = view.findViewById(R.id.pinTitle);
        pinImage = view.findViewById(R.id.pinImage);
        noTags = view.findViewById(R.id.noTags);
        noComments = view.findViewById(R.id.noComments);
        tagRecyclerView = view.findViewById(R.id.tag_recycler_view);
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        pinVideo = view.findViewById(R.id.pinVideo);
        playBtn = view.findViewById(R.id.playBtn);
        /*NOTE: This field is transparent on top of pinVideo to make VideoView react to clicks.
        * Because using onTouchListener directly on VideoView detected multiple touches.*/
        pinVideoView = view.findViewById(R.id.pinVideoView);

        /*Set the pin title view.*/
        pinTitle.setText(pin.getTitle());

        /*Set the pin image views.*/
        if(pin.getImage()!=null){
            pinImage.setVisibility(View.VISIBLE);
            pinVideo.setVisibility(View.INVISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinImage.setImageURI(pin.getImage());
        /*Set the pin video view.*/
        }else if(pin.getVideo()!=null){
            pinImage.setVisibility(View.INVISIBLE);
            pinVideo.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinVideo.setVideoURI(pin.getVideo());
            /*https://stackoverflow.com/questions/3263736/playing-a-video-in-videoview-in-android*/
            pinVideo.start();
            /*Set the transparent image view on top of the video view with a click listener.*/
            pinVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pinVideo.isPlaying()){
                        pinVideo.pause();
                        playBtn.setVisibility(View.VISIBLE);
                    }else {
                        pinVideo.start();
                        playBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });
            /*Check when the video view has completed playing.*/
            pinVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playBtn.setVisibility(View.VISIBLE);
                }
            });
        }

        /*Set the tag list recycler view.*/
        if(tagList.size()>0) {
            /*Create the adapter.*/
            tagListAdapter = new TagListAdapter(tagList);
            /*Create the recycler view.*/
            tagRecyclerView.setAdapter(tagListAdapter);
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            noTags.setVisibility(View.INVISIBLE);
        }else{
            noTags.setVisibility(View.VISIBLE);
        }

        /*Set the comment list recycler view.*/
        if(commentList.size()>0) {
            /*Create the adapter.*/
            commentListAdapter = new CommentListAdapter(commentList);
            /*Create the recycler view.*/
            commentRecyclerView.setAdapter(commentListAdapter);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            noComments.setVisibility(View.INVISIBLE);
        }else{
            noComments.setVisibility(View.VISIBLE);
        }

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        listener = (InteractionListener) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        listener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreatOptionsMenu");
        /*Clear the menu from the previous fragment.*/
        menu.clear();
    }
}
