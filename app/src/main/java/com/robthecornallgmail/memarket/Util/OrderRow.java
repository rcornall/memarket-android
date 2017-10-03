package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 12/09/17.
 */
public class OrderRow {
    public Integer mOrderID;
    public Integer mUserID;
    public String mName;
    public Integer mMemeID;
    public Integer mAmount;
    public Integer mPrice;
    public String mDate;

    public OrderRow (Integer orderID, Integer userID, String name, Integer memeID, Integer amount, Integer price, String date) {
        mOrderID = orderID;
        mUserID = userID;
        mName = name;
        mMemeID = memeID;
        mAmount = amount;
        mPrice = price;
        mDate = date;
    }
}
