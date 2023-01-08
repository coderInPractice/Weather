package edu.learn.weather;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WeatherApplication extends Application {
    private static WeatherApplication sWeatherApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        if(sWeatherApplication == null){
            sWeatherApplication = this;
        }
    }

    public static WeatherApplication getInstance(){
        return sWeatherApplication;
    }

//    public static boolean hasNetwork(){
//        return sWeatherApplication.isNetworkConnected();
//    }
//
//    private boolean isNetworkConnected(){
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//    }
}
