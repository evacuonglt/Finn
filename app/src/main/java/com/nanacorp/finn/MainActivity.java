package com.nanacorp.finn;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nanacorp.finn.api.FinnApiEndpointInterface;
import com.nanacorp.finn.entity.FinnPortfolioData;
import com.nanacorp.finn.view.FinnChartView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {

    private List<FinnPortfolioData> mChartData = null;
    private FinnChartView mFinnChartView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finndemo-bf1ec.firebaseio.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        FinnApiEndpointInterface apiService =
                retrofit.create(FinnApiEndpointInterface.class);
        Call<List<FinnPortfolioData>> call = apiService.getChartData();
        call.enqueue(new Callback<List<FinnPortfolioData>>() {
            @Override
            public void onResponse(@NonNull Call<List<FinnPortfolioData>> call, @NonNull Response<List<FinnPortfolioData>> response) {
                int statusCode = response.code();
                switch (statusCode) {
                    case 200:
                        mChartData = response.body();
                        if (mFinnChartView != null) {
                            Log.d("tuancuong", "setChartData ");
                            mFinnChartView.setChartData(mChartData);
                            //mFinnChartView.setVisibility(View.VISIBLE);
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<FinnPortfolioData>> call, Throwable t) {
                // Log error here since request failed
            }
        });

        mFinnChartView = (FinnChartView) this.findViewById(R.id.main_chart_view);
    }
}