package com.blovvme.wundergroud.View.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.blovvme.wundergroud.Model.HourlyForecast;
import com.blovvme.wundergroud.Model.HourlyForecastOrdered;
import com.blovvme.wundergroud.Model.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivityPresenter implements MainActivityContract.Presenter {

    public static final String BASE_SCHEMA_WND = "http";
    public static final String BASE_URL_WND = "api.wunderground.com";
    public static final String KEY_WND = "900d43f0ce12261e";
    public static final String PATH_WND = "api/" + KEY_WND + "/conditions/hourly10day/q/CA";
    public static final String EXT_WND = ".json";
    private static final String TAG = "MainPresenter";
    FusedLocationProviderClient fusedLocationProviderClient;
    MainActivityContract.View view;
    Context context;
    Activity activity;
    Location currentLocation;
    List<Address> addresses;

    public void attachView(MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }


    @Override
    public void init(Activity activity,Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLocation = location;
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            view.getZipCode(addresses.get(0).getPostalCode().toString());
                            Log.d(TAG, "onSuccess: " + addresses.get(0).getPostalCode() +
                                    " " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

    }

    @Override
    public void restCall(String zipCode, Boolean flag) {
        if (zipCode != null) {
            try {
                OkHttpClient client = new OkHttpClient();
                HttpUrl url = new HttpUrl.Builder()
                        .scheme(BASE_SCHEMA_WND)
                        .host(BASE_URL_WND)
                        .addPathSegments(PATH_WND + zipCode + EXT_WND)
                        .build();
                Log.d(TAG, "getGeocodeAddress: " + url.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();
                        final Weather weather = gson.fromJson(response.body().string(), Weather.class);
                        if (weather.getCurrentObservation() != null) {
                            ((MainActivity) view).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.currentWeather(weather.getCurrentObservation());
                                    OrderHourlyForecast(weather.getHourlyForecast());
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Invalid Zip Code", Toast.LENGTH_SHORT).show();
            }

        } else
            Toast.makeText(context, "Invalid Zip Code", Toast.LENGTH_SHORT).show();
    }

    public void OrderHourlyForecast(List<HourlyForecast> hourlyForecast) {
        List<HourlyForecastOrdered> hourlyForecastOrdered_list = new ArrayList<>();

        List<HourlyForecast> hourlyForecasts_to_order = new ArrayList<>();
        for (int i = 0; i < hourlyForecast.size(); i++) {
            if (hourlyForecast.get(i).getFCTTIME().getCivil().equals("11:00 PM")) {
                hourlyForecasts_to_order.add(hourlyForecast.get(i));
                String label = hourlyForecast.get(i).getFCTTIME().getWeekdayName()
                        + "   " + hourlyForecast.get(i).getFCTTIME().getMonPadded()
                        + "/" + hourlyForecast.get(i).getFCTTIME().getMdayPadded();
                hourlyForecastOrdered_list.add(new HourlyForecastOrdered(label, hourlyForecasts_to_order));
                hourlyForecasts_to_order = new ArrayList<>();
            } else
                hourlyForecasts_to_order.add(hourlyForecast.get(i));
        }

        if (!hourlyForecasts_to_order.isEmpty()) {
            int i = hourlyForecast.size() - 1;
            String date = hourlyForecast.get(i).getFCTTIME().getWeekdayName()
                    + "   " + hourlyForecast.get(i).getFCTTIME().getMonPadded()
                    + "/" + hourlyForecast.get(i).getFCTTIME().getMdayPadded();
            hourlyForecastOrdered_list.add(new HourlyForecastOrdered(date, hourlyForecasts_to_order));
        }
        hourlyForecastOrdered_list.get(0).setLabel("Today");
        hourlyForecastOrdered_list.get(1).setLabel("Tomorrow");
        view.nextWeather(hourlyForecastOrdered_list);
    }

}
