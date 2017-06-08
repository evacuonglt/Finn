package com.nanacorp.finn.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lcuong on 6/6/17.
 *
 * Chart data entity
 */

public class FinnPortfolioData {
    @SerializedName("navs")
    @Expose
    public List<FinnDaily> dailies = new ArrayList<>();
    @SerializedName("portfolioId")
    @Expose
    public String portfolioId;

    public Map<String,List<FinnDaily>> monthlyClassifyData;

    public static final List<String> MONTHS = Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
    public static final List<String> QUARTER = Arrays.asList("MAR", "JUN", "SEP", "DEC");
    public static final int DAY_DISP_PLOT_NUM = 21;


    private void classifyData() {
        if (monthlyClassifyData != null) {
            return;
        }

        monthlyClassifyData = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        List<List<FinnDaily>> monthData = new ArrayList<>();

        for (int i = 0; i < MONTHS.size(); i++) {
            List<FinnDaily> eachMonth = new ArrayList<>();
            monthData.add(eachMonth);
        }
        for (FinnDaily daily : dailies) {
            calendar.setTime(daily.date);
            int currentMonth = calendar.get(Calendar.MONTH);
            monthData.get(currentMonth).add(daily);
        }

        for (int i = 0; i < MONTHS.size(); i++) {
            monthlyClassifyData.put(MONTHS.get(i), monthData.get(i));
        }
    }

    public Map<String,List<FinnDaily>> getDataDaily(String month) {

        Map<String,List<FinnDaily>> data = new HashMap<>();

        if (!MONTHS.contains(month)) {
            return null;
        }

        if (monthlyClassifyData == null) {
            classifyData();
        }
        data.put(month, monthlyClassifyData.get(month));
        return data;
    }

    public Map<String,List<FinnDaily>> getDataMonthly() {

        if (monthlyClassifyData == null) {
            classifyData();
        }

        Calendar calendar = Calendar.getInstance();
        Map<String,List<FinnDaily>> data = new HashMap<>();
        for (String month : MONTHS) {
            List<FinnDaily> monthDataList = monthlyClassifyData.get(month);
            if (monthDataList != null && monthDataList.size() > 0) {
                calendar.setTime(monthDataList.get(0).date);
                int targetId = Math.min(monthDataList.size(), calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) - 1;
                data.put(month, Collections.singletonList(monthDataList.get(targetId)));
            }
        }
        return data;
    }

    public Map<String,List<FinnDaily>> getDataQuarter() {

        if (monthlyClassifyData == null) {
            classifyData();
        }

        Calendar calendar = Calendar.getInstance();
        Map<String,List<FinnDaily>> data = new HashMap<>();
        for (String quarter : QUARTER) {
            List<FinnDaily> monthDataList = monthlyClassifyData.get(quarter);
            if (monthDataList != null && monthDataList.size() > 0) {
                calendar.setTime(monthDataList.get(0).date);
                int targetId = Math.min(monthDataList.size(), calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) - 1;
                data.put(quarter, Collections.singletonList(monthDataList.get(targetId)));
            }
        }
        return data;
    }
}
