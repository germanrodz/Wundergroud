package com.blovvme.wundergroud.Injection.mainActivity;

import com.blovvme.wundergroud.View.MainActivity.MainActivity;

import dagger.Component;

@Component(modules = MainActivityModule.class)
public interface  MainActivityComponent {

    void inject(MainActivity mainActivity);
}

