package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 26/08/17.
 */

public class BagGrid {
    public String mName;
    public Integer mAmount;
    public String mDescription;
    public Boolean mIsEquipped;

    public BagGrid(String name, Integer amount, String description, Boolean isequipped) {
        this.mName = name;
        this.mAmount = amount;
        this.mDescription = description;
        this.mIsEquipped = isequipped;
    }
}
