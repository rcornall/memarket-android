package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MemeObject;
import com.robthecornallgmail.memarket.Util.OrderRow;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListFragment  extends Fragment {
    String TAG = "OrdersListFragment";
    List<OrderRow> mOrderRows;
    OrdersListAdapter mAdapter;
    OnOrdersListFragmentInteractionListener mListener;
    TextView mTitle, mOrderMaker, mNothingHere;
    ImageView mIcon;
    Button mPlaceBuyOrder;
    Boolean mIsBuy;

    public OrdersListFragment() {
        mOrderRows = new ArrayList<>();
        mIsBuy = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new OrdersListAdapter(mOrderRows, mIsBuy, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
//        view.setBackgroundColor(getResources().getColor(R.color.colorMyGrey)); /* covers up drop down arrow */
        mTitle = (TextView) view.findViewById(R.id.orders_text_title);
        mOrderMaker = (TextView) view.findViewById(R.id.order_maker);
        mIcon = (ImageView) view.findViewById(R.id.orders_meme_icon);
        mPlaceBuyOrder = (Button) view.findViewById(R.id.new_buy_order_button);
        mNothingHere = (TextView) view.findViewById(R.id.orders_nothing_here);
        RecyclerView rview = (RecyclerView) view.findViewById(R.id.orders_list_rv);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

//        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
//        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
//        p.y = 50;
//        getDialog().getWindow().setAttributes(p);

        return view;
    }

    public void updateList(HashMap<Integer, OrderRow> orderIDtoRow, Integer selectedMemeID, MemeObject memeObject, String sell) {
        mOrderRows.clear();
        mTitle.setText(sell + " orders for " + WordUtils.capitalize(memeObject.mName));
        if(sell.toUpperCase().equals("SELL")) {
            mPlaceBuyOrder.setText("PLACE BUY ORDER");
            mOrderMaker.setText("SELLER");
            mIsBuy = true;
        } else {
            mPlaceBuyOrder.setText("PLACE SELL ORDER");
            mOrderMaker.setText("BUYER");
            mIsBuy = false;
        }
        mAdapter.setBuyOrSell(mIsBuy);
        String iconName = "icon_" + memeObject.mName.replaceAll(" ", "_").toLowerCase();


        try {
            int iconId = getContext().getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
            Picasso.with(getContext()).load(iconId).centerCrop().fit().into(mIcon);
        } catch (Exception e ) {
            Log.v(TAG,e.toString());
        }

        Log.v(TAG, "updateListCalled");
        for(Map.Entry<Integer, OrderRow> order: orderIDtoRow.entrySet()) {
            order.getValue().mOrderID = order.getKey();
            if(order.getValue().mMemeID.equals(selectedMemeID)) {
                mOrderRows.add(order.getValue());
            }
        }
        if(mOrderRows.isEmpty())
        {
            mNothingHere.setText("No orders right now...");
        }
        else
        {
            mNothingHere.setText(null);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void clearList() {
        mOrderRows.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrdersListFragmentInteractionListener) {
            mListener = (OnOrdersListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrdersListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnOrdersListFragmentInteractionListener {
        void onOrdersListFragmentInteraction(OrderRow orderRow);
    }
}
