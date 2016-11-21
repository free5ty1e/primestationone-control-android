package com.chrisprime.primestationonecontrol.fragments;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity;
import com.chrisprime.primestationonecontrol.utilities.PreferenceStore;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by cpaian on 7/26/15.
 */
public class BaseFragment extends Fragment {
    protected Bus mEventBus = PrimeStationOneControlApplication.eventBus;

    @Inject
    PreferenceStore mPreferenceStore;

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    /**
     * Runs the Runnable on the UI thread and discard the runnable if the activity has been nulled out.
     *
     * @param runnable a Runnable to run if the activity isn't null
     */
    protected void safeRunOnUiThread(@NonNull Runnable runnable) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(runnable);
        }
    }

    /**
     * Runs the Runnable on the I/O thread and discard the runnable if the activity has been nulled out.
     *
     * @param runnable a Runnable to run if the activity isn't null
     */
    protected void safeRunOnIoThread(@NonNull Runnable runnable) {
        final PrimeStationOneControlActivity activity = (PrimeStationOneControlActivity) getActivity();
        if (activity != null) {
            activity.runOnIoThread(runnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }
}
