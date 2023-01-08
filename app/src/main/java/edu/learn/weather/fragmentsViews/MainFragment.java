package edu.learn.weather.fragmentsViews;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.learn.weather.R;
import edu.learn.weather.modals.WeatherDataModel;
import edu.learn.weather.retrofitClient.WeatherClient;
import edu.learn.weather.utils.Utils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragment extends Fragment {

    private static final String API_KEY = "";
    private static final String TAG = MainFragment.class.getCanonicalName();

    private TextView mTempTv;
    private TextView mHumidityTv;
    private TextView mFeelsLikeTv;
    private TextView mWindSpeed;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTempTv = view.findViewById(R.id.temperature_tv);
        mFeelsLikeTv = view.findViewById(R.id.weatherDesc_tv);
        mHumidityTv = view.findViewById(R.id.humidityValue_tv);
        mWindSpeed = view.findViewById(R.id.windValue_tv);

        WeatherClient.getInstance()
                .getWeatherApi()
                .getWeather("Pune", API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<WeatherDataModel>() {
                            @Override
                            public void onSubscribe(
                                    @io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                                Log.d(TAG, "onSubscribe: called");
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onNext(
                                    @io.reactivex.rxjava3.annotations.NonNull
                                            WeatherDataModel weatherDataModel) {
                                Log.d(TAG, "onNext: called");

                                int temp = Utils.convertKelvinToCelsius(weatherDataModel.getMain().getTemp());

                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(temp + "o");
                                spannableStringBuilder.setSpan(new SuperscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                mTempTv.setText(spannableStringBuilder);
                                mFeelsLikeTv.setText(weatherDataModel.getWeather().get(0).getMain());
                                mHumidityTv.setText(weatherDataModel.getMain().getHumidity() + "%");
                                mWindSpeed.setText(weatherDataModel.getWind().getSpeed() + " km/h");

                            }

                            @Override
                            public void onError(
                                    @io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e(TAG, "onError: called");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: called");
                            }
                        });
    }
}