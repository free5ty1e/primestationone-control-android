package com.chrisprime.primestationonecontrol.activities;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.squareup.otto.Bus;

/**
 * Created by cpaian on 7/26/15.
 */
public class BaseEventBusPreferenceActivity extends PreferenceActivity {
    protected Bus mEventBus = PrimeStationOneControlApplication.eventBus;

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }
}

