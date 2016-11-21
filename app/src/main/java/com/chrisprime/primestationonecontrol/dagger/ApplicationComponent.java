package com.chrisprime.primestationonecontrol.dagger;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity;
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModule;
import com.chrisprime.primestationonecontrol.dagger.modules.PreferencesModule;
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule;
import com.chrisprime.primestationonecontrol.fragments.BaseFragment;
import com.chrisprime.primestationonecontrol.utilities.PreferenceStore;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component for setting what gets injected
 * Created by cpaian on 8/24/16.
 */
@SuppressWarnings("WeakerAccess")
@Singleton
@Component(modules = {ApplicationContextModule.class, PreferencesModule.class, ThreadPoolModule.class})
public interface ApplicationComponent {
    void inject(PrimeStationOneControlApplication primeStationOneControlApplication);

    void inject(BaseFragment baseFragment);

    void inject(PrimeStationOneControlActivity primeStationOneControlActivity);

    void inject(PreferenceStore preferencesStore);

    ExecutorService threadPoolExecutor();
}
