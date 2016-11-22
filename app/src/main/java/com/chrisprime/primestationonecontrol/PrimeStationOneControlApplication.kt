package com.chrisprime.primestationonecontrol

import android.app.Application
import android.content.Context
import android.util.Log

import com.chrisprime.primestationonecontrol.dagger.Injector
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import com.squareup.leakcanary.LeakCanary
import com.squareup.otto.Bus

import timber.log.Timber

class PrimeStationOneControlApplication : Application() {

    var currentPrimeStationOne: PrimeStationOne? = null

    companion object {
        val ID_EVENT_BUS_MAIN = "main"
        lateinit var instance: PrimeStationOneControlApplication
        lateinit var eventBus: Bus
        val appResourcesContext: Context
            get() = instance.applicationContext
    }

    init {
        instance = this
        eventBus = Bus(ID_EVENT_BUS_MAIN)
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        Injector.initializeApplicationComponent(this)

//        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
/*
        } else {
            Timber.plant(CrashReportingTree())
        }
*/

        val buildType = if (BuildConfig.DEBUG) "debug" else "production"
        Timber.d("Launching " + buildType + " build version " + BuildConfig.VERSION_NAME + ", which is version code " + BuildConfig.VERSION_CODE)

        updateCurrentPrimeStationFromJson()
    }

    fun updateCurrentPrimeStationFromJson() {
        currentPrimeStationOne = FileUtilities.readJsonCurrentPrimestation(this)
    }


    /**
     * A tree which logs important information for crash reporting.
     */
    private class CrashReportingTree : Timber.Tree() {


        override fun log(priority: Int, tag: String, message: String, t: Throwable) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
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
