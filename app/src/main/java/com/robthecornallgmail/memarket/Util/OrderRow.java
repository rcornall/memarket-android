package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 12/09/17.
 */
public class OrderRow {
    public Integer mUserID;
    public String mName;
    public Integer mMemeID;
    public Integer mAmount;
    public Integer mPrice;
    public String mDate;

    public OrderRow (Integer userID, String name, Integer memeID, Integer amount, Integer price, String date) {
        mUserID = userID;
        mName = name;
        mMemeID = memeID;
        mAmount = amount;
        mPrice = price;
        mDate = date;
    }
}
