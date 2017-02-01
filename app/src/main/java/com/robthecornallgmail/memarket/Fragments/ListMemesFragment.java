package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MemeRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListMemesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<MemeRow> memeRowList;
    private MemesListAdapter mAdapter;
    EditText mSearchView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListMemesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListMemesFragment newInstance(int columnCount) {
        ListMemesFragment fragment = new ListMemesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meme_list, container, false);
        memeRowList = new ArrayList<>();
        // Set the adapter
        mAdapter = new MemesListAdapter(memeRowList, mListener);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(mAdapter);
        }

//        prepareMemeRowData();
        return view;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void prepareMemeRowData() {
        MemeRow movie = new MemeRow("Mad Max: Fury Road", 25);
        memeRowList.add(movie);

        movie = new MemeRow("Inside Out", 3);
        memeRowList.add(movie);

        movie = new MemeRow("Star Wars: Episode VII - The Force Awakens", 5);
        memeRowList.add(movie);

        movie = new MemeRow("Shaun the Sheep", 234);
        memeRowList.add(movie);

        movie = new MemeRow("The Martian", 234);
        memeRowList.add(movie);

        movie = new MemeRow("Mission: Impossible Rogue Nation", 3);
        memeRowList.add(movie);

        movie = new MemeRow("Up", 4);
        memeRowList.add(movie);

        movie = new MemeRow("Star Trek",67);
        memeRowList.add(movie);

        movie = new MemeRow("The LEGO MemeRow", 77);
        memeRowList.add(movie);

        movie = new MemeRow("Iron Man", 77);
        memeRowList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    public void updateList(Map<String, Integer> map) {

        for (Map.Entry<String , Integer> entry: map.entrySet()) {
            MemeRow meme = new MemeRow(entry.getKey(), entry.getValue());
            memeRowList.add(meme);
            mAdapter.mItemsCopy.add(meme);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void filter(String text) {
        mAdapter.filter(text);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MemeRow row);
    }
}
