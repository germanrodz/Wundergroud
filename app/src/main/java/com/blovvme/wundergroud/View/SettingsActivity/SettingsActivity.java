package com.blovvme.wundergroud.View.SettingsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.blovvme.wundergroud.Injection.settingsActivity.DaggerSettingsActivityComponent;
import com.blovvme.wundergroud.R;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity implements SettingsActivityContract.View {
    private static final String MY_PREF_FILE = "mypref_file";
    Toolbar toolbar2;
    EditText etZipCode;
    Spinner spinner;
    @Inject
    SettingsActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        etZipCode = (EditText) findViewById(R.id.etZipCode) ;
        spinner = (Spinner) findViewById(R.id.spinner);
        setSupportActionBar(toolbar2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setupDagger();
        presenter.attachView(this);
        setToolbarBackPressed();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharePreferences = getSharedPreferences(MY_PREF_FILE, Context.MODE_PRIVATE);
        etZipCode.setText(sharePreferences.getString("zip","0"));
        spinner.setSelection(adapter.getPosition(sharePreferences.getString("unit","Fahrenheit")));

    }

    private void setupDagger() {
        DaggerSettingsActivityComponent.create().inject(this);
    }

    private void setToolbarBackPressed() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Umbrella settings");
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",etZipCode.getText().toString()+","+ spinner.getSelectedItem().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public void showError(String s) {

    }
}
