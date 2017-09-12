package com.blovvme.wundergroud.Injection.mainActivity;

import com.blovvme.wundergroud.View.MainActivity.MainActivityPresenter;


import dagger.Module;
import dagger.Provides;


@Module
public class MainActivityModule {

    @Provides
    MainActivityPresenter providerMainActivityPresenter(){return new MainActivityPresenter();
    }
}
