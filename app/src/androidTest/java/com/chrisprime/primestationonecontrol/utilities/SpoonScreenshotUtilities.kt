package com.chrisprime.primestationonecontrol.utilities

import android.app.Activity
import android.text.TextUtils
import com.jraska.falcon.FalconSpoon
import com.squareup.spoon.Spoon
import timber.log.Timber
import java.io.File
import java.net.URLEncoder


@Suppress("unused")
object SpoonScreenshotUtilities {

    @JvmStatic fun screenshot(tag: String) {
        screenshot(tag, TestUtilities.currentActivity)
    }

    @JvmStatic fun screenshot(tag: String, className: String, methodName: String) {
        screenshot(tag, TestUtilities.currentActivity, className, methodName)
    }

    @JvmStatic fun screenshot(tag: String, currentActivity: Activity?) {
        screenshot(tag, currentActivity, null, null)
    }

    @JvmStatic private fun screenshot(tag: String, currentActivity: Activity?, className: String?, methodName: String?) {
        val sanitizedTag = safeStringToScreenshotName(tag)
        Timber.d(".screenshot(%s) requested!  Sanitized tag to %s", tag, sanitizedTag)
        try {
            if (currentActivity != null) {
                if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName)) {
                    FalconSpoon.screenshot(currentActivity, sanitizedTag)
                } else {
                    FalconSpoon.screenshot(currentActivity, sanitizedTag, className, methodName)
                }
                saveData(sanitizedTag, currentActivity)
            } else {
                Timber.w(".screenshot(%s): Unable to proceed, currentActivity is null!", tag)
            }
        } catch (e: RuntimeException) {
            Timber.e(e, ".screenshot(%s): Unable to proceed, we might not have permission to write to the external storage: %s, is this API 23+?", tag, e.message)
        }
    }

    @JvmStatic private fun saveData(tag: String, currentActivity: Activity) {
//        Timber.d(".saveData(%s) requested!", tag)
//        val contextWrapper = ContextWrapper(currentActivity)
//        val databaseFile = contextWrapper.getDatabasePath(SchemaValidator.DATABASE_NAME)
//        val realmFile = File(String.format("%s%s%s", contextWrapper.filesDir, File.separator, "db.realm"))
        val sharedPrefsFile = File(String.format("%s%s%s%s%s%s%s%s%s%s", File.separator, "data", File.separator, "data", File.separator, "com.chrisprime.primestationonecontrol.debug", File.separator, "shared_prefs", File.separator, "com.chrisprime.primestationonecontrol.debug_preferences.xml"))

//        saveFileToReport(currentActivity, databaseFile, tag)
//        saveFileToReport(currentActivity, realmFile, tag)
        saveFileToReport(currentActivity, sharedPrefsFile, tag)
    }

    @JvmStatic private fun saveFileToReport(currentActivity: Activity, file: File, tag: String) {
        try {
            Spoon.save(currentActivity, file) //Grab the database so it can easily be downloaded and examined from the Spoon test reoprt too!
            Timber.d(".screenshot(%s): successfully saved %s snapshot!", tag, file.absolutePath)

        } catch (e: RuntimeException) {
            Timber.w(".screenshot(%s): Unable to save from path %s - does not yet exist? %s", tag, file.absolutePath, e.message)
        }
    }

    @JvmStatic private fun safeStringToScreenshotName(string: String) = URLEncoder.encode(string, "UTF-8").replace('%', '_').replace('+', '_').replace('.', '_')
}
