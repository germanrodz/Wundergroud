package com.blovvme.wundergroud.Injection.settingsActivity;

import com.blovvme.wundergroud.View.SettingsActivity.SettingsActivity;

import dagger.Component;


@Component(modules = SettingsActivityModule.class)  //@Component(modules = 1.class,2.class) separated by commas for 2 or more modules
public interface SettingsActivityComponent {

//    void inject(SettingsActivity SettingsActivity); no difference between inject or insert because is the name of the method only in here
    void inject(SettingsActivity SettingsActivity);

}
