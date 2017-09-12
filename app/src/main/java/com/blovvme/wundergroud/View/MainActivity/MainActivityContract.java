package com.blovvme.wundergroud.View.MainActivity;

import android.app.Activity;
import android.content.Context;

import com.blovvme.wundergroud.BasePresenter;
import com.blovvme.wundergroud.BaseView;
import com.blovvme.wundergroud.Model.CurrentObservation;
import com.blovvme.wundergroud.Model.HourlyForecastOrdered;

import java.util.List;

public interface MainActivityContract {
    interface View extends BaseView{
        void getZipCode(String zipCode);
        void currentWeather(CurrentObservation weather);
        void nextWeather(List<HourlyForecastOrdered> hourlyForecastOrdered);
    }
    interface Presenter extends BasePresenter<View> {
        void init(Activity activity, Context context);
        void getLocation();
        void restCall(String zipCode, Boolean flag);
    }

}
