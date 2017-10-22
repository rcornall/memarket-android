package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.robthecornallgmail.memarket.R;

/**
 * Created by rob on 21/10/17.
 */

public class NewMemeFragment extends Fragment {
    private static final String TAG = "NewMemeFrag";
    private Button mBackButton, mDoneButton;


    public NewMemeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_meme, container, false);


        return view;
    }




}