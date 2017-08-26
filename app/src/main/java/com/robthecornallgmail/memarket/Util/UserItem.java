package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 26/08/17.
 */

public class UserItem {
    public Integer mAmount;
    public Boolean mIsEquipped;
    public UserItem(Integer amount, Integer isEquipped)
    {
        mAmount = amount;

        mIsEquipped = isEquipped.equals(1);
    }
}
