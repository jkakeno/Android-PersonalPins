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
    ImageView boardImage;
    InputMethodManager imgr;

    public BoardEditFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        /*Create a new board_item object.*/
        board = new Board();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.board_edit_fragment, container, false);
        final EditText boardTitle = view.findViewById(R.id.boardTitle);
        boardImage = view.findViewById(R.id.boardImage);
        Button cancel = view.findViewById(R.id.cancel);
        Button save = view.findViewById(R.id.save);
        Button enter = view.findViewById(R.id.enter);

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
                /*Set board_item title.*/
                board.setTitle(boardTitle.getText().toString());
                /*Dismiss the keyboard.*/
                imgr.hideSoftInputFromWindow(boardTitle.getWindowToken(), 0);
            }
        });

        /*Initially notify main activity that the boar image is not clicked.*/
        listener.onBoardEditBoardImageInteraction(false);

        /*Set the fragment image view with the default image boardUri.*/
        /*https://stackoverflow.com/questions/2928904/how-to-set-the-bitmap-to-the-imageview-in-main-xml-captured-from-the-camera*/
        boardImage.setImageBitmap(MainActivity.boardBitmap);
        /*Set the board_item object boardUri with the default boardUri.*/
        board.setImage(MainActivity.boardBitmap);

        /*Once the board_item image is clicked notify main activity that it was clicked. */
        boardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBoardEditBoardImageInteraction(true);

            }
        });

        /*Notify the main activity that cancel button is clicked.*/
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBoardEditCancelInteraction(true);
            }
        });

        /*Notify the main activity that save button is clicked. And pass the board_item with the title set.*/
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(boardTitle.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(),"Enter a board title...", Toast.LENGTH_SHORT).show();
                }else {
                    listener.onBoardEditSaveInteraction(board);
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
        Log.d(TAG, "Uri: "+String.valueOf(MainActivity.boardUri));

        /*Set the fragment image view with the new image boardUri.*/
        /*https://stackoverflow.com/questions/2928904/how-to-set-the-bitmap-to-the-imageview-in-main-xml-captured-from-the-camera*/
        boardImage.setImageBitmap(MainActivity.boardBitmap);
        /*Set the board_item object boardUri with the new boardUri.*/
        board.setImage(MainActivity.boardBitmap);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        listener = null;
    }
}
