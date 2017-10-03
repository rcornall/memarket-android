package com.robthecornallgmail.memarket.Util;

import java.util.Date;

/**
 * Created by rob on 30/09/17.
 */

public class ActiveRow {
    public Integer mOrderID;
    public Boolean mIsBuy;
    public Integer mMemeID;
    public String mMemeName;
    public Integer mAmount;
    public Integer mPrice;
    public Date mDate;

    public ActiveRow (Boolean isBuy, Integer orderID,  Integer memeID, String memeName, Integer amount, Integer price, Date date) {
        mIsBuy = isBuy;
        mOrderID = orderID;
        mMemeID = memeID;
        mMemeName = memeName;
        mAmount = amount;
        mPrice = price;
        mDate = date;
    }
}