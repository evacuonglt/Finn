package com.nanacorp.finn.api;

import com.nanacorp.finn.entity.FinnPortfolioData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by lcuong on 6/6/17.
 *
 * API interface
 */

public interface FinnApiEndpointInterface {
    @GET("demo.json")
    Call<List<FinnPortfolioData>> getChartData();
}
