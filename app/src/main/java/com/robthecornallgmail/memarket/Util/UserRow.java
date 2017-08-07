package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 06/08/17.
 */

public class UserRow {
    private String mUsername;
    private Integer mMoney;

    public UserRow(){    }

    public UserRow(String name, int money) {
        mUsername = name;
        mMoney = money;
    }

    public String getName() {return mUsername;}
    public Integer getMoney() {return mMoney;}

    public void setName(String name) {mUsername = name;}
    public void setMoney(int money) {mMoney = money;}

}