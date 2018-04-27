package com.example.personalpins.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalpins.Model.Board;
import com.example.personalpins.R;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {

    private static final String ARG = "boardList";
    private ArrayList<Board> boardList;

    public ViewPagerFragment() {
    }

    /*Store the received data from main activity in the static field ARG.*/
    public static ViewPagerFragment newInstance(ArrayList<Board> boardList) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG, boardList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            /*Get the list of boards from static field.*/
            boardList = getArguments().getParcelableArrayList(ARG);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Inflate the view that will be display when this fragment is created.*/
        View view = inflater.inflate(R.layout.viewpager_fragment,container, false);

        /*Create the fragments for the view pager.*/
        final PinSearchFragment searchFragment = new PinSearchFragment();
        /*Instantiate BoradFragment.newInstance() and pass the boardList.*/
        final BoardListFragment boardFragment = BoardListFragment.newInstance(boardList);

        /*Set the view pager.*/
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                searchFragment.setArguments(getArguments());
                boardFragment.setArguments(getArguments());
                return position == 0 ? searchFragment : boardFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Search" : "Boards";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        /*Set the tabs.*/
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
