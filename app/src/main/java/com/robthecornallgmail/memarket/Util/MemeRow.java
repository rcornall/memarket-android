package com.robthecornallgmail.memarket.Util;

import android.support.v7.widget.util.SortedListAdapterCallback;

/**
 * Created by rob on 31/01/17.
 */

public class MemeRow{
    private String mName;
    private Integer mPrice;


    public MemeRow() {
    }

    public MemeRow(String name, Integer price) {
        this.mName = name;
        this.mPrice = price;
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

    public void setPrice(Integer price) {
        this.mPrice = price;
    }
}
