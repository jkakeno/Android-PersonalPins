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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.personalpins.DataBase;
import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.R;

import java.util.ArrayList;


public class PinListFragment extends Fragment {

    private static final String ARG1 = "pinList";
    private static final String ARG2 = "boardId";
    private static final String TAG = PinListFragment.class.getSimpleName();
    private ArrayList<Pin> pinList;
    InteractionListener listener;
    PinListAdapter adapter;
    GridLayoutManager layoutManager;
    RecyclerView recyclerView;
    View view;
    DataBase db;
    long boardId;
    TextView noPins;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinListFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static PinListFragment newInstance(long boardId ,ArrayList<Pin> pinList) {
        PinListFragment fragment = new PinListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG1, pinList);
        args.putLong(ARG2, boardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        if (getArguments() != null) {
            /*Get the list of pins and board id to which the pin list corresponds to from static field.*/
            pinList = getArguments().getParcelableArrayList(ARG1);
            boardId = getArguments().getLong(ARG2);
        }

        db = new DataBase(getActivity());

        /*Create menu item in fragment.*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.pin_list_fragment, container, false);
        noPins = view.findViewById(R.id.noPins);

        if(pinList.size()>0) {
            /*Create the adapter.*/
            adapter = new PinListAdapter(pinList, listener);
            layoutManager = new GridLayoutManager(getActivity(), 2);

            /*Create the recycler view.*/
            recyclerView = (RecyclerView) view.findViewById(R.id.pin_recycler_view);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            noPins.setVisibility(View.INVISIBLE);
        }else{
            noPins.setVisibility(View.VISIBLE);
        }

        /*Set the fab.*/
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        /*Notify main activity that fab is clicked.*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPinListFragmentFabInteraction(true);
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

        pinList = db.getPinList(boardId);

        if(pinList.size()>0) {
                /*Create the adapter.*/
            adapter = new PinListAdapter(pinList, listener);
            layoutManager = new GridLayoutManager(getActivity(), 2);

                /*Create the recycler view.*/
            recyclerView = (RecyclerView) view.findViewById(R.id.pin_recycler_view);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            noPins.setVisibility(View.INVISIBLE);
        }else{
            noPins.setVisibility(View.VISIBLE);
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
        /*Clear the menu from the previous fragment.*/
        menu.clear();
    }
}
