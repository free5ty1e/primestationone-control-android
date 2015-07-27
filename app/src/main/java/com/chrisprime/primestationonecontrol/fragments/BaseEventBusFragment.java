package com.chrisprime.primestationonecontrol.fragments;

import android.support.v4.app.Fragment;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.squareup.otto.Bus;

/**
 * Created by cpaian on 7/26/15.
 */
public class BaseEventBusFragment extends Fragment {
    protected Bus mEventBus = PrimeStationOneControlApplication.getEventBus();

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
