package com.robthecornallgmail.memarket.Util;

import android.support.v7.widget.util.SortedListAdapterCallback;

/**
 * Created by rob on 31/01/17.
 */

public class MemeRow{
    private String mName;
    private Integer mPrice;
    private Integer mLastPrice;


    public MemeRow() {
    }

    public MemeRow(String name, Integer price, Integer lastPrice) {
        this.mName = name;
        this.mPrice = price;
        this.mLastPrice = lastPrice;
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
