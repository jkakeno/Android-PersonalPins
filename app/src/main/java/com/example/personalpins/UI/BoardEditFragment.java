package com.example.personalpins.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.MainActivity;
import com.example.personalpins.Model.Board;
import com.example.personalpins.R;

public class BoardEditFragment extends Fragment{

    private static final String TAG = BoardEditFragment.class.getSimpleName();
    InteractionListener listener;
    Board board;
    InputMethodManager imgr;

    View view;
    ImageView boardImage;
    EditText boardTitle;
    Button cancel;
    Button save;
    Button enter;

    boolean titleEntered;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BoardEditFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        /*Create a new board object.*/
        board = new Board();

        /*Initially set false because this is turn true only when enter btn is pressed.*/
        titleEntered=false;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        /*Inflate all the views.*/
        view = inflater.inflate(R.layout.board_edit_fragment, container, false);
        boardTitle = view.findViewById(R.id.boardTitle);
        boardImage = view.findViewById(R.id.boardImage);
        cancel = view.findViewById(R.id.cancel);
        save = view.findViewById(R.id.save);
        enter = view.findViewById(R.id.enter);

        /*Initially notify main activity that the boar image is not clicked.*/
        listener.onBoardEditBoardImageInteraction(false);

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

        /*Display key board.
        * https://stackoverflow.com/questions/10508363/show-keyboard-for-edittext-when-fragment-starts*/
        imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imgr != null) {
            imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        /*Request focus on the board title edit text so that user can start typing as soon as fragment is opened.*/
        boardTitle.requestFocus();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Set board object title.*/
                board.setTitle(boardTitle.getText().toString());
                /*Dismiss the keyboard.*/
                imgr.hideSoftInputFromWindow(boardTitle.getWindowToken(), 0);
                /*Turn to true.*/
                titleEntered = true;
            }
        });


        /*Set the fragment image view.*/
        boardImage.setImageURI(MainActivity.boardUri);
        /*Set the board object image.*/
        board.setImage(MainActivity.boardUri);

        boardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Hide the keyboard.*/
                imgr.hideSoftInputFromWindow(boardTitle.getWindowToken(), 0);
                /*Notify main activity that the image view is clicked.*/
                listener.onBoardEditBoardImageInteraction(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Notify the main activity that cancel button is clicked.*/
                listener.onBoardEditCancelInteraction(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boardTitle.getText().toString().isEmpty() && titleEntered){
                    /*Notify the main activity that save button is clicked and pass the board object.*/
                    listener.onBoardEditSaveInteraction(board);
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
