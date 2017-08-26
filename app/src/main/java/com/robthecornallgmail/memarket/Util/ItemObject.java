package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 25/08/17.
 */

public class ItemObject {
    public String mName;
    public String mDescription;
    public Integer mPrice;
    public Integer mMaxAmount;
    public Integer mType;
    public Integer mSubtype;

    public ItemObject(String name, String description, Integer price,
               Integer maxAmount, Integer type, Integer subType)
    {
        mName = name;
        mDescription = description;
        mPrice = price;
        mMaxAmount = maxAmount;
        mType = type;
        mSubtype = subType;
    }

}
