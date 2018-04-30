package com.example.personalpins.UI;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.R;

import java.util.ArrayList;


public class PinSearchFragment extends Fragment {

    private static final String ARG = "pinList";
    private static final String TAG = PinSearchFragment.class.getSimpleName();
    private ArrayList<Pin> pinList;
    private InteractionListener listener;
    PinSearchAdapter adapter;
    GridLayoutManager layoutManager;
    RecyclerView recyclerView;
    InputMethodManager imgr;

    View view;
    TextView noPins;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinSearchFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static PinSearchFragment newInstance(ArrayList<Pin> pinList) {
        PinSearchFragment fragment = new PinSearchFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG, pinList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pinList = getArguments().getParcelableArrayList(ARG);
        }

        /*Create menu items for this fragment.*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Inflate all views.*/
        view = inflater.inflate(R.layout.pin_search_fragment, container, false);
        noPins = view.findViewById(R.id.noPins);

        /*Initialize InputMethodManager to use to hide keyboard later.*/
        imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(pinList!=null){
            if(pinList.size()>0) {
                /*Create the adapter.*/
                adapter = new PinSearchAdapter(pinList, listener);
                layoutManager = new GridLayoutManager(getActivity(), 2);
                /*Create the recycler view.*/
                recyclerView = (RecyclerView) view.findViewById(R.id.pin_recycler_view);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
                noPins.setVisibility(View.INVISIBLE);
            }
        }else{
            noPins.setVisibility(View.VISIBLE);
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

        /*Update the recycler view.*/
        if(pinList!=null){
            if(pinList.size()>0) {
                /*Create the adapter.*/
                adapter = new PinSearchAdapter(pinList, listener);
                layoutManager = new GridLayoutManager(getActivity(), 2);
                /*Create the recycler view.*/
                recyclerView = (RecyclerView) view.findViewById(R.id.pin_recycler_view);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
                noPins.setVisibility(View.INVISIBLE);
            }
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
         /*Inflate the new fragment menu from resource.*/
        inflater.inflate(R.menu.search_menu,menu);

        /*Initialize search menu on action bar.*/
        MenuItem searchViewItem = menu.findItem(R.id.search);
        final SearchView searchViewActionBar = (SearchView) searchViewItem.getActionView();
        searchViewActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                listener.onPinSearchFragmentSearchQueryInteraction(query);

                searchViewActionBar.clearFocus();

                /*Dismiss the keyboard.*/
                imgr.hideSoftInputFromWindow(searchViewActionBar.getWindowToken(), 0);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
