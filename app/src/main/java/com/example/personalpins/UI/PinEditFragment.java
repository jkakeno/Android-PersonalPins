package com.example.personalpins.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.personalpins.DataBase;
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
    Pin pin;
    ImageView pinImage;
    Tag tag;
    Comment comment;
    ArrayList<Tag> tagList;
    ArrayList<Comment> commentList;
    RecyclerView tagRecyclerView;
    RecyclerView commentRecyclerView;
    TagListAdapter tagListAdapter;
    CommentListAdapter commentListAdapter;
    View view;
    TextView noTags;
    TextView noComments;
    InputMethodManager imgr;
    EditText pinTitle;
    Button cancel;
    Button save;
    Button enter;
    ImageButton tagBtn;
    ImageButton commentBtn;
    Board board;
    DataBase db;

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

        /*Create a new board_item object.*/
        pin = new Pin();



//        board = new Board();

        if (getArguments() != null) {
            board = getArguments().getParcelable(ARG);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
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

        pin.setBoardId(String.valueOf(board.getId()));

        /*Set the fragment image view with the default image boardUri.*/
        pinImage.setImageBitmap(MainActivity.pinBitmap);
        /*Set the board_item object boardUri with the default boardUri.*/
        pin.setImage(MainActivity.pinBitmap);

        /*Display key board.*/
        imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imgr != null) {
            imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        /*Request focus on the board title edit text so that user can start typing as soon as fragment is opened.*/
        pinTitle.requestFocus();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Set board_item title.*/
                pin.setTitle(pinTitle.getText().toString());
                /*Dismiss the keyboard.*/
                imgr.hideSoftInputFromWindow(pinTitle.getWindowToken(), 0);
            }
        });
        tagList = new ArrayList<>();
        tagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tag = new Tag();
                /*Create dialog to enter tag.*/
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
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

                        /*Set the tag and add it to the list of tags.*/
                        tag.setTag(editText.getText().toString());
                        tagList.add(tag);
                        pin.setTagList(tagList);

                        if(tagList.size()>0) {
                            tagListAdapter = new TagListAdapter(tagList);
                            tagRecyclerView.setAdapter(tagListAdapter);
                            tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            noTags.setVisibility(View.INVISIBLE);
                        }else{
                            noTags.setVisibility(View.VISIBLE);
                        }

                        Log.d(TAG,"Tag List Size: "+tagList.size());

                        /*TODO: Add an item to the tags recycler view.*/
                        /*Add a tag to the data base.*/

                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                    }
                });

                /*Set negative button action.*/
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
        commentList = new ArrayList<>();
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                comment = new Comment();
                /*Create dialog to enter comment.*/
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
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

                        /*Create a topic.*/
                        comment.setComment(editText.getText().toString());
                        commentList.add(comment);
                        pin.setCommentList(commentList);

                        if(commentList.size()>0) {
                            commentListAdapter = new CommentListAdapter(commentList);
                            commentRecyclerView.setAdapter(commentListAdapter);
                            commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            noComments.setVisibility(View.INVISIBLE);
                        }else{
                            noComments.setVisibility(View.VISIBLE);
                        }

                        Log.d(TAG,"Comment List Size: "+commentList.size());
                        /*TODO: Add an item to the comments recycler view.*/
                        /*Add a comment to the data base.*/

                        /*Dismiss the keyboard.*/
                        imgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                    }
                });

                /*Set negative button action.*/
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });


        /*Notify the main activity that cancel button is clicked.*/
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPinEditCancelInteraction(true);
            }
        });

        /*Notify the main activity that save button is clicked. And pass the board_item with the title set.*/
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinTitle.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(),"Enter a pin title...", Toast.LENGTH_SHORT).show();
                }else {
                    listener.onPinEditSaveInteraction(pin);
                }
            }
        });

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

        /*Set the fragment image view with the new image boardUri.*/
        /*https://stackoverflow.com/questions/2928904/how-to-set-the-bitmap-to-the-imageview-in-main-xml-captured-from-the-camera*/
        pinImage.setImageBitmap(MainActivity.pinBitmap);
        /*Set the board_item object boardUri with the new boardUri.*/
        pin.setImage(MainActivity.pinBitmap);

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        listener = null;
    }
}
