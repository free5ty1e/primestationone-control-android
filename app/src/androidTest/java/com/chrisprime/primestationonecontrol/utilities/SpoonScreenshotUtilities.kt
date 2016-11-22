package com.chrisprime.primestationonecontrol.utilities

import android.app.Activity

import com.squareup.spoon.Spoon

import timber.log.Timber

object SpoonScreenshotUtilities {
    var sScreenshotActivity: Activity? = null

    fun screenshot(tag: String, className: String, methodName: String) {
        screenshot(tag, sScreenshotActivity, className, methodName)
    }

    @JvmOverloads fun screenshot(tag: String, currentActivity: Activity? = sScreenshotActivity, className: String? = null, methodName: String? = null) {
        Timber.d(".screenshot(%s) requested!", tag)
        if (currentActivity != null) {
            if (StringUtilities.isEmpty(className) || StringUtilities.isEmpty(methodName)) {
                Spoon.screenshot(currentActivity, tag)
            } else {
                Spoon.screenshot(currentActivity, tag, className, methodName)
            }
            saveDatabase(tag, currentActivity)
        } else {
            Timber.w(".screenshot(%s): Unable to proceed, currentActivity is null!", tag)
        }
    }

    fun saveDatabase(tag: String, currentActivity: Activity) {
        Timber.d(".saveDatabase(%s) requested!", tag)
        //        File databaseFile = new ContextWrapper(currentActivity).getDatabasePath(DATABASE_NAME);
        //        try {
        //            Spoon.save(currentActivity, databaseFile); //Grab the database upon failure so it can easily be downloaded and examined from the Spoon test reoprt too!
        //            Timber.d(".screenshot(%s): successfully saved database snapshot!", tag);
        //        } catch (RuntimeException e) {
        //            Timber.w(".screenshot(%s): Unable to save database from path %s - does not yet exist?", tag, databaseFile.getAbsolutePath());
        //        }
    }
}
