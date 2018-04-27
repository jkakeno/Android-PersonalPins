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

import com.example.personalpins.InteractionListener;
import com.example.personalpins.R;

import java.util.ArrayList;


public class PinSearchFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = PinSearchFragment.class.getSimpleName();
    private int mColumnCount = 2;
    private InteractionListener listener;
    public String saveQuery;
    ArrayList<String> searchQuery;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinSearchFragment() {
    }

    // TODO: Get the list of pins from main activity
    public static PinSearchFragment newInstance(int columnCount) {
        PinSearchFragment fragment = new PinSearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        /*Create menu items for this fragment.*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_search_fragment, container, false);

        /*Create dummy data.*/
        getPinList(saveQuery);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new PinSearchAdapter(searchQuery, listener));
        }



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
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

                Log.d(TAG, "Search query: " + query);

                saveQuery = query;
                /*TODO:Make a tag_item query to the data base.*/

                searchViewActionBar.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public ArrayList<String> getPinList(String query) {
        searchQuery = new ArrayList<>();
        for(int i=0; i < 10 ; i++){
            searchQuery.add(query);
            if(query != null) {
                Log.d("query: ", query);
            }
        }
        return searchQuery;
    }
}
