package com.blovvme.wundergroud.View.MainActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blovvme.wundergroud.Injection.mainActivity.DaggerMainActivityComponent;
import com.blovvme.wundergroud.Model.CurrentObservation;
import com.blovvme.wundergroud.Model.HourlyForecastOrdered;
import com.blovvme.wundergroud.R;
import com.blovvme.wundergroud.View.SettingsActivity.SettingsActivity;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final String MY_PREF_FILE = "mypref_file";
    @Inject
    MainActivityPresenter presenter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    private String zip,code;
    private String unit = "Fahrenheit";
    private RecyclerView rvDays;
    private TextView tvTemperature, tvCondition;
    private LinearLayout toolbar_header_view;
    private Toolbar myToolbar;
    private CurrentObservation currentObservation;
    private DaysAdapter daysAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar_header_view = (LinearLayout) findViewById(R.id.toolbar_header_view);
        rvDays = (RecyclerView) findViewById(R.id.rvDays);
        setSupportActionBar(myToolbar);

        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvCondition = (TextView) findViewById(R.id.tvCondition);
        setupDagger();
        presenter.attachView(this);
        presenter.init(this,this);
        checkPermissions();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setupDagger() {
        DaggerMainActivityComponent.create().inject(this);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            presenter.getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.getLocation();
                } else {
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);
                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("zip",zip);
                editor.putString("unit",unit);
                editor.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showError(String s) {

    }

    @Override
    public void getZipCode(String zipCode) {
        this.zip = zipCode;
        presenter.restCall(zip, false);
    }

    @Override
    public void currentWeather(CurrentObservation weather) {
        this.currentObservation =  weather;
        if(unit.equals("Celsius")){
            tvTemperature.setText(currentObservation.getTempC() + "°C");
        }
        else
            tvTemperature.setText(currentObservation.getTempF() + "°F");
        //tvTemperature.setText(weather.getTempF() + "°F");
        tvCondition.setText(weather.getWeather());
        setTitle(weather.getDisplayLocation().getFull());
        if (Double.parseDouble(weather.getTempF().toString()) < 60) {
            myToolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(12, 162, 249)));
            toolbar_header_view.setBackgroundDrawable(new ColorDrawable(Color.rgb(12, 162, 249)));
        } else {
            myToolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(247, 146, 12)));
            toolbar_header_view.setBackgroundDrawable(new ColorDrawable(Color.rgb(247, 146, 12)));
        }

    }

    @Override
    public void nextWeather(List<HourlyForecastOrdered> hourlyForecastOrdered) {
        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        rvDays.setLayoutManager(layoutManager);
        rvDays.setItemAnimator(itemAnimator);

        daysAdapter = new DaysAdapter(hourlyForecastOrdered);
        daysAdapter.setUnits(unit);
        rvDays.setAdapter(daysAdapter);
        daysAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String values = data.getStringExtra("result");
        code = values.split(",")[0];
        unit = values.split(",")[1];
        if(!code.equals(""))
            zip = code;
        if (zip != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.restCall(zip, true);
                    daysAdapter.setUnits(unit);
                    if(unit.equals("Celsius")){
                        tvTemperature.setText(currentObservation.getTempC() + "°C");
                    }
                    else
                        tvTemperature.setText(currentObservation.getTempF() + "°F");
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
