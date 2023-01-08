package edu.learn.weather.retrofitClient;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import edu.learn.weather.WeatherApplication;
import edu.learn.weather.retrofitClient.WeatherInterface.WeatherApiInterface;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {

    private static final String TAG = "ServiceGenerator";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";

    private static WeatherClient sWeatherClient;

    private WeatherClient() {
        //To make singleton
    }

    public static WeatherClient getInstance(){
        if(sWeatherClient == null){
            sWeatherClient = new WeatherClient();
        }
        return sWeatherClient;
    }

    private static final long cacheSize = 5 * 1024 * 1024; // 5 MB


    private static Retrofit retrofit(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    private static OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
                .addNetworkInterceptor(networkInterceptor()) // only used when network is on
                .build();
    }

    private static Cache cache(){
        return new Cache(new File(WeatherApplication.getInstance().getCacheDir(),"someIdentifier"), cacheSize);
    }

    /**
     * This interceptor will be called both if the network is available and if the network is not available
     * @return Interceptor
     */
    private static Interceptor offlineInterceptor() {
//        return chain -> {
//            Log.d(TAG, "offline interceptor: called.");
//            Request request = chain.request();
//
//            // prevent caching when network is on. For that we use the "networkInterceptor"
//            if (!WeatherApplication.hasNetwork()) {
//                CacheControl cacheControl = new CacheControl.Builder()
//                        .maxStale(7, TimeUnit.DAYS)
//                        .build();
//
//                request = request.newBuilder()
//                        .removeHeader(HEADER_PRAGMA)
//                        .removeHeader(HEADER_CACHE_CONTROL)
//                        .cacheControl(cacheControl)
//                        .build();
//            }
//
//            return chain.proceed(request);
//        };
        return null;
    }

    /**
     * This interceptor will be called ONLY if the network is available
     * @return Interceptor
     */
    private static Interceptor networkInterceptor() {
        return chain -> {
            Log.d(TAG, "network interceptor: called.");

            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(5, TimeUnit.SECONDS)
                    .build();

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    private static Interceptor httpLoggingInterceptor ()
    {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(message -> Log.d(TAG, "log: http log: " + message));
        httpLoggingInterceptor.setLevel( HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    public WeatherApiInterface getWeatherApi(){
        return retrofit().create(WeatherApiInterface.class);
    }

}
