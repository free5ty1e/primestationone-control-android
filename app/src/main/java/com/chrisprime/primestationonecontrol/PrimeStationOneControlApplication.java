package com.chrisprime.primestationonecontrol;

import android.app.Application;
import android.util.Log;

import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.FileUtilities;
import com.squareup.otto.Bus;

import timber.log.Timber;

public class PrimeStationOneControlApplication extends Application {
    public static final String ID_EVENT_BUS_MAIN = "main";

    private static PrimeStationOneControlApplication sInstance;
    public static PrimeStationOneControlApplication getInstance() {
        return sInstance;
    }

    private static Bus sBus;
    public static Bus getEventBus() {
        return sBus;
    }

    private PrimeStationOne mCurrentPrimeStationOne;

    //This only gets started by the os so my singleton looks a little weird for this class
    public PrimeStationOneControlApplication() {
        super();
        sInstance = this;
        sBus = new Bus(ID_EVENT_BUS_MAIN);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        String buildType = BuildConfig.DEBUG ? "debug" : "production";
        Timber.d("Launching " + buildType + " build version " + BuildConfig.VERSION_NAME + ", which is version code " + BuildConfig.VERSION_CODE);

        updateCurrentPrimeStationFromJson();
    }

    public void updateCurrentPrimeStationFromJson() {
        mCurrentPrimeStationOne = FileUtilities.readJsonCurrentPrimestation(this);
    }


    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {


        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

//            FakeCrashLibrary.log(priority, tag, message);
//
//            if (t != null) {
//                if (priority == Log.ERROR) {
//                    FakeCrashLibrary.logError(t);
//                } else if (priority == Log.WARN) {
//                    FakeCrashLibrary.logWarning(t);
//                }
//            }
        }
    }

    public PrimeStationOne getCurrentPrimeStationOne() {
        return mCurrentPrimeStationOne;
    }

    public void setCurrentPrimeStationOne(PrimeStationOne mCurrentPrimeStationOne) {
        this.mCurrentPrimeStationOne = mCurrentPrimeStationOne;
    }
}
