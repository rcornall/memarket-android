package com.robthecornallgmail.memarket.Util;

import android.support.v7.widget.util.SortedListAdapterCallback;

/**
 * Created by rob on 31/01/17.
 */

public class MemeRow{
    public Integer mID;
    public String mName;
    public Integer mPrice;
    public Integer mLastPrice;


    public MemeRow() {
    }

    public MemeRow(Integer id, String name, Integer price, Integer lastPrice) {
        mID = id;
        mName = name;
        mPrice = price;
        mLastPrice = lastPrice;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public Integer getPrice() {
        return mPrice;
    }
    public Integer getLastPrice() {
        return mLastPrice;
    }

}
