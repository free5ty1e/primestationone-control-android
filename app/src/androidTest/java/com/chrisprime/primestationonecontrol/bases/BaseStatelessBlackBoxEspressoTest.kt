package com.chrisprime.primestationonecontrol.bases

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.core.deps.guava.collect.Iterables
import android.support.test.runner.AndroidJUnit4
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.test.ActivityInstrumentationTestCase2

import org.junit.runner.RunWith

/**
 * Created by cpaian on 7/16/15.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseStatelessBlackBoxEspressoTest<T : Activity>(clazz: Class<T>) : ActivityInstrumentationTestCase2<T>(clazz) {
    protected var mActivity: Activity? = null

    protected val currentActivity: Activity
        get() {
            waitForIdleSync()
            val activity = arrayOfNulls<Activity>(1)
            try {
                runTestOnUiThread {
                    val activites = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                    activity[0] = Iterables.getOnlyElement(activites)
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }

            return activity[0]!!
        }

    protected fun waitForIdleSync() {
        val instrumentation = instrumentation
        instrumentation?.waitForIdleSync()
    }
}
