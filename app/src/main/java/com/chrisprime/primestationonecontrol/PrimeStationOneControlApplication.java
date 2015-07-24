package com.chrisprime.primestationonecontrol;

import android.app.Application;
import android.util.Log;

import timber.log.Timber;

public class PrimeStationOneControlApplication extends Application {

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
}
