package com.chrisprime.primestationonecontrol.dagger.modules;


import android.content.Context;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationContextModule {

    private final PrimeStationOneControlApplication sApplication;

    public ApplicationContextModule(PrimeStationOneControlApplication application) {
        sApplication = application;
    }

    @Provides
    @Singleton
    public PrimeStationOneControlApplication application() {
        return sApplication;
    }

    @Provides
    @Singleton
    public Context applicationContext() {
        return sApplication.getApplicationContext();
    }
}
