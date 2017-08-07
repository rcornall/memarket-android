package com.robthecornallgmail.memarket.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.UserRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by rob on 06/08/17.
 */

public class LeaderboardDialogFragment extends  DialogFragment{
    private String TAG = "Dialogfragment";
    private LeaderboardListAdapter mAdapter;
    private List<UserRow> mUserRows;
    private OnLeaderboardFragmentInteractionListener mListener;

    public LeaderboardDialogFragment() {
        mUserRows = new ArrayList<>();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new LeaderboardListAdapter(mUserRows, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard, container, false);
        view.setBackgroundColor(getResources().getColor(R.color.colorMyGrey));

        RecyclerView rview = (RecyclerView) view.findViewById(R.id.leaderboard_recycler_view);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

        return view;
    }





    public void updateList(Map<String, Integer> stocks) {
        if (mUserRows != null && !mUserRows.isEmpty()) {
            mUserRows.clear();
        }
        for (Map.Entry<String, Integer> entry : stocks.entrySet()) {
            UserRow meme = new UserRow(entry.getKey(), entry.getValue());
            mUserRows.add(meme);
            Log.v(TAG, "userrows: " + meme.getName());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLeaderboardFragmentInteractionListener) {
            mListener = (OnLeaderboardFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLeaderboardFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLeaderboardFragmentInteractionListener {
        void onLeaderboardFragmentInteraction(UserRow userRow);
    }
}
