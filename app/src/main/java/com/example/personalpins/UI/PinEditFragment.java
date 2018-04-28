package com.example.personalpins.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.MainActivity;
import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Comment;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.Model.Tag;
import com.example.personalpins.R;

import java.util.ArrayList;

public class PinEditFragment extends Fragment{

    private static final String TAG = PinEditFragment.class.getSimpleName();
    private static final String ARG = "board";
    InteractionListener listener;
    Board board;
    Pin pin;
    Tag tag;
    Comment comment;
    ArrayList<Tag> tagList;
    ArrayList<Comment> commentList;
    RecyclerView tagRecyclerView;
    RecyclerView commentRecyclerView;
    TagListAdapter tagListAdapter;
    CommentListAdapter commentListAdapter;
    InputMethodManager imgr;

    View view;
    ImageView pinImage;
    ImageView pinVideoView;
    ImageView playBtn;
    VideoView pinVideo;
    TextView noTags;
    TextView noComments;
    EditText pinTitle;
    Button cancel;
    Button save;
    Button enter;
    ImageButton tagBtn;
    ImageButton commentBtn;

    boolean titleEntered;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinEditFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static PinEditFragment newInstance(Board board) {
        PinEditFragment fragment = new PinEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG, board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        /*Create a new pin object.*/
        pin = new Pin();
        /*Create an empty tag list.*/
        tagList = new ArrayList<>();
        /*Create an empty comment list.*/
        commentList = new ArrayList<>();

        if (getArguments() != null) {
            /*Get the data from static field ARG.*/
            board = getArguments().getParcelable(ARG);
        }

        /*Initially set false because this is turn true only when enter btn is pressed.*/
        titleEntered=false;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        /*Inflate all the views.*/
        view = inflater.inflate(R.layout.pin_edit_fragment, container, false);
        pinTitle = view.findViewById(R.id.pinTitle);
        pinImage = view.findViewById(R.id.pinImage);
        cancel = view.findViewById(R.id.cancel);
        save = view.findViewById(R.id.save);
        enter = view.findViewById(R.id.enter);
        tagBtn = view.findViewById(R.id.tag);
        commentBtn = view.findViewById(R.id.comment);
        noTags = view.findViewById(R.id.noTags);
        noComments = view.findViewById(R.id.noComments);
        tagRecyclerView = view.findViewById(R.id.tag_recycler_view);
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        pinVideo = view.findViewById(R.id.pinVideo);
        /*NOTE: This field is transparent on top of pinVideo to make VideoView react to clicks.
        * Because using onTouchListener directly on VideoView detected multiple touches.*/
        pinVideoView = view.findViewById(R.id.pinVideoView);
        playBtn = view.findViewById(R.id.playBtn);

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

        /*Set the board id (foreign key) of the pin object.*/
        pin.setBoardId(String.valueOf(board.getId()));

        /*Set the pin image view.*/
        if(MainActivity.selectedMedia.equals("Photo")){
            pinImage.setVisibility(View.VISIBLE);
            pinVideo.setVisibility(View.INVISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinImage.setImageURI(MainActivity.pinImageUri);
            /*Set the pin object image.*/
            pin.setImage(MainActivity.pinImageUri);
        /*Set the pin video view.*/
        }else if(MainActivity.selectedMedia.equals("Video")){
            pinImage.setVisibility(View.INVISIBLE);
            pinVideo.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.INVISIBLE);
            pinVideo.setVideoURI(MainActivity.pinVideoUri);
            pinVideo.start();
            /*Set the pin object video.*/
            pin.setVideo(MainActivity.pinVideoUri);
        }

        /*Display key board.*/
        imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imgr != null) {
            imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            pinTitle.requestFocus();
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Set board object title.*/
                pin.setTitle(pinTitle.getText().toString());
                /*Dismiss the keyboard.*/
                imgr.hideSoftInputFromWindow(pinTitle.getWindowToken(), 0);
                /*Set to true.*/
                titleEntered = true;
            }
        });

        pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Hide the keyboard.*/
                imgr.hideSoftInputFromWindow(pinTitle.getWindowToken(), 0);
            }
        });
        /*Set the transparent image view on top of the video view with a click listener.*/
        pinVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Hide the keyboard.*/
                imgr.hideSoftInputFromWindow(pinTitle.getWindowToken(), 0);
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

        tagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Create a new tag object.*/
                tag = new Tag();
                /*Create dialog to enter tag.*/
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.tag_dialog_layout, null);
                dialogBuilder.setView(dialogView);

                final EditText editText = (EditText) dialogView.findViewById(R.id.dialogEdittext);

                /*Display key board.*/
                imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imgr != null) {
                    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                /*Request cursor focus on the edit text so that user can start typing as soon as fragment is opened.*/
                editText.requestFocus();

                dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /*Set the text of the tag object.*/
                        tag.setTag(editText.getText().toString());
                        /*Add the tag object to the list.*/
                        tagList.add(tag);
                        /*Set the pin's tag list.*/
                        pin.setTagList(tagList);

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
                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                    }
                });

                /*Set negative button action.*/
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Create a new comment object.*/
                comment = new Comment();
                /*Create dialog to enter comment.*/
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.comment_dialog_layout, null);
                dialogBuilder.setView(dialogView);

                final EditText editText = (EditText) dialogView.findViewById(R.id.dialogEdittext);

                /*Display key board.*/
                imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imgr != null) {
                    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                /*Request cursor focus on the edit text so that user can start typing as soon as fragment is opened.*/
                editText.requestFocus();

                dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /*Set the text of the comment object..*/
                        comment.setComment(editText.getText().toString());
                        /*Add the comment object to the list.*/
                        commentList.add(comment);
                        /*Set the pin's comment list.*/
                        pin.setCommentList(commentList);

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
                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                    }
                });

                /*Set negative button action.*/
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Notify the main activity that cancel button is clicked.*/
                listener.onPinEditCancelInteraction(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pinTitle.getText().toString().isEmpty() && titleEntered){
                    /*Notify the main activity that save button is clicked and pass the pin object.*/
                    listener.onPinEditSaveInteraction(pin);
                }else{
                    Toast.makeText(getActivity(),"Enter a title and press + ...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        listener = null;
    }
}
