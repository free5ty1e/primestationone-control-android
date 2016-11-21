package com.chrisprime.primestationonecontrol.dagger;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModule;
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule;

import java.util.Objects;

public class Injector {

    protected static ApplicationComponent sApplicationComponent;

    Injector() {
    }

    public static void initializeApplicationComponent(PrimeStationOneControlApplication snkrsApplication) {
        sApplicationComponent = DaggerApplicationComponent.builder()
                .applicationContextModule(new ApplicationContextModule(snkrsApplication))
                .threadPoolModule(new ThreadPoolModule())
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        Objects.requireNonNull(sApplicationComponent, "sApplicationComponent is null");
        return sApplicationComponent;
    }
}
