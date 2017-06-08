package com.nanacorp.finn.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by lcuong on 6/6/17.
 * <p>
 * Daily change Entity
 */

public class FinnDaily {

    @SerializedName("amount")
    @Expose
    public float amount;
    @SerializedName("date")
    @Expose
    public Date date;

    public float getAmount() {
        return amount;
    }
}
