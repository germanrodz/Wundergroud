package com.blovvme.wundergroud.Injection.settingsActivity;

import com.blovvme.wundergroud.View.SettingsActivity.SettingsActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsActivityModule {

    @Provides
    SettingsActivityPresenter providerSettingsActivityPresenter(){
        return new SettingsActivityPresenter();
    }
}
