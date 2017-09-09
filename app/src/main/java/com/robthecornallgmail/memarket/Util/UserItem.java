package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 26/08/17.
 */

public class UserItem {
    public Integer mItemID;
    public Integer mAmount;
    public Boolean mIsEquipped;
    public Integer mXCoordinate;
    public Integer mWorkingAt;
    public Integer mWorkingPosition;
    public Integer mWorkerLevel;
    public UserItem(Integer itemID, Integer amount, Integer isEquipped, Integer x_coordinate,
                    Integer workingAt, Integer workingPosition, Integer workerLevel)
    {
        mItemID = itemID;
        mAmount = amount;
        mIsEquipped = isEquipped.equals(1);
        mXCoordinate = x_coordinate;
        mWorkingAt = workingAt;
        mWorkingPosition = workingPosition;
        mWorkerLevel = workerLevel;
    }
}
