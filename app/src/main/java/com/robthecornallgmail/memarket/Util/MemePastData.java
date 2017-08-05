package com.robthecornallgmail.memarket.Util;

import com.robthecornallgmail.memarket.Activities.DateRange;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by rob on 27/01/17.
 */

public class MemePastData
{
    // if we previously got the data before, then no need to request from the server again,
    // just use the data.
    private Boolean doneAlready = false;
    public MemePastData()
    {
        this.doneAlready = false;
        pastYearData = new TreeMap<>();
    }

    public void setDoneAlready()
    {
        this.doneAlready = true;
    }
    public void setNotDone() { this.doneAlready = false; }

    public Boolean  isDoneAlready()
    {
        return this.doneAlready;
    }

    // could have made these private with public accessors but it was easier this way to loop through..

    public TreeMap<Date, Integer> pastYearData;

//    public void putPastData(Date key, Integer val, DateRange range)
//    {
//        if (range == DateRange.DAY) {
//            this.pastDayData.put(key,val);
//        } else if (range == DateRange.WEEK) {
//            this.pastWeekData.put(key,val);
//        } else if (range == DateRange.MONTH) {
//            this.pastMonthData.put(key,val);
//        } else if (range == DateRange.YEAR) {
//            this.pastYearData.put(key,val);
//        }
//    }




}
