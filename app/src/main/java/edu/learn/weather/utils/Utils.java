package edu.learn.weather.utils;

import android.util.Log;

public class Utils {

    private static final String TAG = Utils.class.getCanonicalName();

    public static int convertKelvinToCelsius(double valueInKelvin) {
        Log.d(TAG, "convertKelvinToCelsius: value " + (valueInKelvin - 273.15));
        return (int) (valueInKelvin - 273.15);
    }
}
