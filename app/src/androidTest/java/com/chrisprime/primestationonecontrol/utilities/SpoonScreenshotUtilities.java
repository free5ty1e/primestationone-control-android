package com.chrisprime.primestationonecontrol.utilities;

import android.app.Activity;

import com.squareup.spoon.Spoon;

import timber.log.Timber;

public class SpoonScreenshotUtilities {
    public static Activity sScreenshotActivity;

    public static void screenshot(String tag) {
        screenshot(tag, sScreenshotActivity);
    }

    public static void screenshot(String tag, String className, String methodName) {
        screenshot(tag, sScreenshotActivity, className, methodName);
    }

    public static void screenshot(String tag, Activity currentActivity) {
        screenshot(tag, currentActivity, null, null);
    }

    public static void screenshot(String tag, Activity currentActivity, String className, String methodName) {
        Timber.d(".screenshot(%s) requested!", tag);
        if (currentActivity != null) {
            if (StringUtilities.INSTANCE.isEmpty(className) || StringUtilities.INSTANCE.isEmpty(methodName)) {
                Spoon.screenshot(currentActivity, tag);
            } else {
                Spoon.screenshot(currentActivity, tag, className, methodName);
            }
            saveDatabase(tag, currentActivity);
        } else {
            Timber.w(".screenshot(%s): Unable to proceed, currentActivity is null!", tag);
        }
    }

    public static void saveDatabase(String tag, Activity currentActivity) {
        Timber.d(".saveDatabase(%s) requested!", tag);
//        File databaseFile = new ContextWrapper(currentActivity).getDatabasePath(DATABASE_NAME);
//        try {
//            Spoon.save(currentActivity, databaseFile); //Grab the database upon failure so it can easily be downloaded and examined from the Spoon test reoprt too!
//            Timber.d(".screenshot(%s): successfully saved database snapshot!", tag);
//        } catch (RuntimeException e) {
//            Timber.w(".screenshot(%s): Unable to save database from path %s - does not yet exist?", tag, databaseFile.getAbsolutePath());
//        }
    }
}
