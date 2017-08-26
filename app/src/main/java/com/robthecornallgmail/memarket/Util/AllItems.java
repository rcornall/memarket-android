package com.robthecornallgmail.memarket.Util;

import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by rob on 25/08/17.
 */

public class AllItems {
    private static SparseArray<ItemObject> itemsIdToObject = new SparseArray<>();
    public static ItemObject getItemFromID(Integer key)
    {
        return itemsIdToObject.get(key);
    }
    public static void putItem(Integer key, String name, String description, Integer price,
                               Integer maxAmount, Integer type, Integer subType)
    {
        itemsIdToObject.append(key,
                new ItemObject(name, description, price, maxAmount, type, subType)
        );
    }
    public static void clear()
    {
        itemsIdToObject.clear();
    }
}
