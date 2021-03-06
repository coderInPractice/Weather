package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    //final String CITY = "VARANASI";

    final int REQUEST_CODE = 123;
    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager myLocation;
    LocationListener myListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(myIntent);
            }
        });

    }


    // TODO: Add onResume() here:
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "onResume() fired");

        Intent intent = getIntent();
        String city= intent.getStringExtra("City");

        if(city != null){
            getWeatherForNewCity(city);
        }else {
            Log.i("onResume", "onLocationChanged() calling");
            getWeatherForCurrentLocation();
        }

    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city){
        RequestParams params2 = new RequestParams();
        params2.put("q",city);
        params2.put("appid",APP_ID);
        letsDoSomeNetworking(params2);
    }


    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        myLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("onLocationChanged", "callback received");
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                Log.i("Latitude", latitude);
                Log.i("Longitude", longitude);

                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                //params.put("query",CITY);
                letsDoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i("providerStatus","DISABLED");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        myLocation.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, myListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("requestStatus", "GRANTED");
            }else{
                Log.e("requestStatus","DENIED");
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    public void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.i("Message Received", response.toString());
                WeatherDataModel weatherData = WeatherDataModel.fromJSON(response);
                assert weatherData != null;
                updateUI(weatherData);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("Failed", e.toString());
                Log.i("Failed Code", Integer.toString(statusCode));
            }
        });
    }



    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){
        mCityLabel.setText(weather.getmCity());
        mTemperatureLabel.setText(weather.getmTemp());
        int resourceID = getResources().getIdentifier(weather.getmIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }



    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();
        if(myLocation != null){
            myLocation.removeUpdates(myListener);
        }
    }
}
