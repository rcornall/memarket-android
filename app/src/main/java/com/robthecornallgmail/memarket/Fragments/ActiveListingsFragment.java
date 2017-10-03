package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.ActiveRow;
import com.robthecornallgmail.memarket.Util.MemeObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 30/09/17.
 */

public class ActiveListingsFragment extends Fragment {
    private String TAG = "ActiveListingsFragment";

    private List<ActiveRow> mActiveRows;
    private ActiveListingsAdapter mAdapter;
    private OnActiveListingsInteractionListener mListener;
    private TextView mNothingHere;

    public ActiveListingsFragment() {
        mActiveRows = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ActiveListingsAdapter(mActiveRows, mListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActiveRows.clear();
        mAdapter.notifyDataSetChanged();
        View view = inflater.inflate(R.layout.fragment_active_listings, container, false);

        mNothingHere = (TextView) view.findViewById(R.id.active_no_orders);

        RecyclerView rview = (RecyclerView) view.findViewById(R.id.active_orders_rview);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActiveListingsInteractionListener) {
            mListener = (OnActiveListingsInteractionListener) context;
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
    public void updateList(List<ActiveRow> activeRows) {
        if(mActiveRows != null && !mActiveRows.isEmpty()) {
            mActiveRows.clear();
        }
        for(ActiveRow row : activeRows) {
            mActiveRows.add(row);
        }
        if(mActiveRows.isEmpty())
        {
            mNothingHere.setText("No active orders right now...");
//            mLayoutRView.setVisibility(View.GONE);
        }
        else
        {
//            mLayoutRView.setVisibility(View.VISIBLE);
            mNothingHere.setText(null);
        }
        mAdapter.notifyDataSetChanged();
    }

    public interface OnActiveListingsInteractionListener {
        void onActiveListingsCancel(ActiveRow row);
    }
}
