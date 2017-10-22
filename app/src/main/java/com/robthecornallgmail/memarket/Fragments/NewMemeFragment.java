package com.robthecornallgmail.memarket.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.Defines.MEME_SIZE;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rob on 21/10/17.
 */

public class NewMemeFragment extends Fragment {
    private static final String TAG = "NewMemeFrag";
    private Button mBackButton, mDoneButton;

    private ImageView mMemePreview;

    private EditText mUrl;

    private SeekBar mSizeBar, mFundingBar;
    private TextView mSizeText, mFundingText;

    private View mView;

    private int mViewWidth;


    public NewMemeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_meme, container, false);
        mViewWidth = mView.findViewById(R.id.new_meme_layout).getWidth();
        mMemePreview = (ImageView) mView.findViewById(R.id.new_meme_preview);

        mUrl = (EditText) mView.findViewById(R.id.imgur_url);
        mUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals(null)) {
                    Picasso.with(mView.getContext()).load(s.toString()).fit().centerCrop().into(mMemePreview);
                }
            }
        });

        mSizeBar = (SeekBar) mView.findViewById(R.id.size_seekbar);
        mSizeText = (TextView) mView.findViewById(R.id.size);
        mFundingBar = (SeekBar) mView.findViewById(R.id.funding_seekbar);
        mFundingText = (TextView) mView.findViewById(R.id.funding);

//        mSizeText.setX(mSizeBar.getX());

        mSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSizeText.setX(seekBar.getX() + seekBar.getThumb().getBounds().left-10);
                if (progress == MEME_SIZE.MEME_SMALL) {
                    mSizeText.setText("SMALL");
                } else if (progress == MEME_SIZE.MEME_MEDIUM) {
                    mSizeText.setText("MEDIUM");
                } else if (progress == MEME_SIZE.MEME_LARGE) {
                    mSizeText.setText("LARGE");
                } else /* (progress == MEME_SIZE.MEME_MAINSTREAM) */ {
                    mSizeText.setX(mViewWidth - mSizeText.getWidth());
                    mSizeText.setText("MAIN-STREAM");

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return mView;
    }



}