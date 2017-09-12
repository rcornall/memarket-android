package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 11/09/17.
 */

public class MemeObject {
    public String mName;
    public Integer mPrice;
    public Integer mLastPrice;
    public Integer mSharesHeld;
    public String mLink;


    public MemeObject(String name, Integer currentStock, Integer lastStock, String link) {
        mName = name;
        mPrice = currentStock;
        mLastPrice = lastStock;
        mLink = link;
    }
}
