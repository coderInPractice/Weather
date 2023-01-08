package edu.learn.weather.retrofitClient.WeatherInterface;

import java.util.List;

import edu.learn.weather.modals.WeatherDataModel;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiInterface {

    @GET("weather")
    Observable<WeatherDataModel> getWeather(
            @Query("q") String search_city,
            @Query("appid") String apiKey
    );
}
