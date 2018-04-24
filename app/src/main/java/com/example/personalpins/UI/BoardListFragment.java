package com.example.personalpins.UI;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.personalpins.DataBase;
import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Board;
import com.example.personalpins.R;

import java.util.ArrayList;


public class BoardListFragment extends Fragment {

    private static final String ARG = "boardList";
    private static final String TAG = BoardListFragment.class.getSimpleName();
    private ArrayList<Board> boardList;
    InteractionListener listener;
    BoardListAdapter adapter;
    GridLayoutManager layoutManager;
    RecyclerView recyclerView;
    View view;
    DataBase db;
    TextView noBoards;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BoardListFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static BoardListFragment newInstance(ArrayList<Board> boardList) {
        BoardListFragment fragment = new BoardListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG, boardList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        if (getArguments() != null) {
            /*Get the list of boards from static field.*/
            boardList = getArguments().getParcelableArrayList(ARG);
        }

        db = new DataBase(getActivity());

        /*Create menu item in fragment.*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.board_list_fragment, container, false);
        noBoards = view.findViewById(R.id.noBoards);

        if(boardList.size()>0) {
        /*Create the adapter.*/
            adapter = new BoardListAdapter(boardList, listener);
            layoutManager = new GridLayoutManager(getActivity(), 2);

        /*Create the recycler view.*/
            recyclerView = (RecyclerView) view.findViewById(R.id.boards_recycler_view);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            noBoards.setVisibility(View.INVISIBLE);
        }else{
            noBoards.setVisibility(View.VISIBLE);
        }

        /*Set the fab.*/
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        /*Notify main activity that fab is clicked.*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBoardListFragmentFabInteraction(true);
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

        boardList=db.getBoardList();

        if(boardList.size()>0) {
        /*Create the adapter.*/
            adapter = new BoardListAdapter(boardList, listener);
            layoutManager = new GridLayoutManager(getActivity(), 2);

        /*Create the recycler view.*/
            recyclerView = (RecyclerView) view.findViewById(R.id.boards_recycler_view);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            noBoards.setVisibility(View.INVISIBLE);
        }else{
            noBoards.setVisibility(View.VISIBLE);
        }
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

        /*Inflate the new fragment menu from resource.*/
        inflater.inflate(R.menu.camera_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.camera){
            listener.onCameraIconInteraction(true);
        }
        return super.onOptionsItemSelected(item);
    }
}
