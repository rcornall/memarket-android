package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.BagGrid;
import com.robthecornallgmail.memarket.Util.ItemObject;
import com.robthecornallgmail.memarket.Util.UserItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 26/08/17.
 */

public class BagGridFragment extends Fragment {
String TAG = "BagGridFragment";
    private int mColumnCount = 3;
    private OnBagGridInteractionListener mListener;
    private List<BagGrid> mBagGridList;
    private BagGridAdapter mAdapter;

    public BagGridFragment() {
        mBagGridList = new ArrayList<>();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.v(TAG, "BagGridFragment created()");
        View view = inflater.inflate(R.layout.fragment_bag, container, false);

        // Set the adapter
        mAdapter = new BagGridAdapter(mBagGridList, mListener);
        if (view instanceof RecyclerView) {
            Log.v(TAG, "did we get hereeeeeeeeeee");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setItemViewCacheSize(20);
//            recyclerView.setDrawingCacheEnabled(true);
//            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBagGridInteractionListener) {
            mListener = (OnBagGridInteractionListener) context;
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

    public void updateList(Map<Integer, UserItem> itemsIDtoUserItems, HashMap<Integer, ItemObject> itemsIDtoObject) {
        Log.v(TAG, "updateList called");
        if (mBagGridList != null && !mBagGridList.isEmpty()) {
            mBagGridList.clear();
        }
        int i = 0;
        for (Map.Entry<Integer, UserItem> i2u : itemsIDtoUserItems.entrySet())
        {
            Integer key = i2u.getKey();
            String description = itemsIDtoObject.get(key).mDescription;
            String name = itemsIDtoObject.get(key).mName;
            BagGrid item = new BagGrid(name, i2u.getValue().mAmount, description, i2u.getValue().mIsEquipped );
            mBagGridList.add(item);
            mAdapter.mItemsCopy.add(item);
            i++;
        }
        for (int j = i;j<18; j++) {
            Log.v(TAG, "asdfasdf");
            BagGrid item = new BagGrid("blank",1,"hii",true);
            mBagGridList.add(item);
            mAdapter.mItemsCopy.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }


    public interface OnBagGridInteractionListener {
        void OnBagGridInteraction(BagGrid row);
    }
}
