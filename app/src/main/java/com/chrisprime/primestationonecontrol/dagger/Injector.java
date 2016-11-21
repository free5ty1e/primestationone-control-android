package com.chrisprime.primestationonecontrol.dagger;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModule;
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule;

public class Injector {

    @SuppressWarnings("WeakerAccess")
    protected static ApplicationComponent sApplicationComponent;

    Injector() {
    }

    public static void initializeApplicationComponent(PrimeStationOneControlApplication primeStationOneControlApplication) {
        sApplicationComponent = DaggerApplicationComponent.builder()
                .applicationContextModule(new ApplicationContextModule(primeStationOneControlApplication))
                .threadPoolModule(new ThreadPoolModule())
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        if (sApplicationComponent == null) {
            throw new NullPointerException(".getApplicationComponent(): sApplicationComponent is null!  Injection broken...");
        }
        return sApplicationComponent;
    }
}
