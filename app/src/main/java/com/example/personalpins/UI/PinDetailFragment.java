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
    ImageView pinImage;
    ArrayList<Tag> tagList;
    ArrayList<Comment> commentList;
    RecyclerView tagRecyclerView;
    RecyclerView commentRecyclerView;
    TagListAdapter tagListAdapter;
    CommentListAdapter commentListAdapter;
    View view;
    TextView noTags;
    TextView noComments;
    TextView pinTitle;
    VideoView pinVideo;
    ImageView playBtn;


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
            pin = getArguments().getParcelable(ARG);
        }
        if(pin!=null) {
            if (pin.getTagList() != null) {
                tagList = pin.getTagList();
                Log.d(TAG, String.valueOf(tagList.size()));
            }
            if (pin.getCommentList() != null) {
                commentList = pin.getCommentList();
                Log.d(TAG, String.valueOf(commentList.size()));
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.pin_detail_fragment, container, false);
        pinTitle = view.findViewById(R.id.pinTitle);
        pinImage = view.findViewById(R.id.pinImage);
        noTags = view.findViewById(R.id.noTags);
        noComments = view.findViewById(R.id.noComments);
        tagRecyclerView = view.findViewById(R.id.tag_recycler_view);
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        pinVideo = view.findViewById(R.id.pinVideo);
        playBtn = view.findViewById(R.id.playBtn);

        pinTitle.setText(pin.getTitle());

        /*Set the fragment image view with the default image boardUri.*/
        if(pin.getImage()!=null){
            pinImage.setVisibility(View.VISIBLE);
            pinVideo.setVisibility(View.INVISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinImage.setImageBitmap(pin.getImage());

        }else if(pin.getVideo()!=null){
            pinImage.setVisibility(View.INVISIBLE);
            pinVideo.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinVideo.setVideoURI(pin.getVideo());
            /*https://stackoverflow.com/questions/3263736/playing-a-video-in-videoview-in-android*/
            pinVideo.start();

            pinVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pinVideo.start();
                    playBtn.setVisibility(View.INVISIBLE);
                }
            });

            pinVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playBtn.setVisibility(View.VISIBLE);
                }
            });

        }

        if(tagList.size()>0) {
            tagListAdapter = new TagListAdapter(tagList);
            tagRecyclerView.setAdapter(tagListAdapter);
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            noTags.setVisibility(View.INVISIBLE);
        }else{
            noTags.setVisibility(View.VISIBLE);
        }


        if(commentList.size()>0) {
            commentListAdapter = new CommentListAdapter(commentList);
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
}
