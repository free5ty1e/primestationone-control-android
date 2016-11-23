package com.chrisprime.primestationonecontrol.bases

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingPolicies
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.chrisprime.primestationonecontrol.utilities.CustomFailureHandler
import com.chrisprime.primestationonecontrol.utilities.PreferenceStore
import com.chrisprime.primestationonecontrol.utilities.SpoonScreenshotUtilities
import com.chrisprime.primestationonecontrol.utilities.TestUtilities
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by cpaian on 7/16/15.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentationTest<T : Activity> @JvmOverloads internal constructor(clazz: Class<T>, clearUserDataBeforeLaunch: Boolean = false) {

    @Rule
    @JvmField
    var mActivityTestRule: ActivityTestRule<T>

    @Inject
    @JvmField
    var preferenceStore: PreferenceStore? = null

    @Inject
    @JvmField
    var mMockWebServer: MockWebServer? = null

    init {

        mActivityTestRule = object : ActivityTestRule<T>(clazz) {
            override fun beforeActivityLaunched() {
                configureTest()
                prepareBeforeActivityLaunchedFirstThing()
                injectComponentsForInstrumentationTest()
                if (clearUserDataBeforeLaunch) {
                    wipeAppData()
                }
                Timber.d(".BaseInstrumentationTest constructor()")
                prepareBeforeActivityLaunchedLastThing()
                super.beforeActivityLaunched()
            }

            override fun afterActivityLaunched() {
                super.afterActivityLaunched()
                Espresso.setFailureHandler(CustomFailureHandler(TestUtilities.currentActivity!!))
            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()
                cleanUpAfterActivityFinishedLastThing()
            }
        }
    }

    fun wipeAppData() {
        preferenceStore!!.clear()
    }

    /**
     * Override this method to NOT mock endpoints for a specific test class
     */
    protected open fun shouldMockEndpoints(): Boolean {
        return true
    }

    protected open fun configureTest() {
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES)
//        RestAdapterModule.setConnectTimeout(5 * TimeManager.ONE_MINUTE_IN_MILLIS.toInt())
//        RestAdapterModule.setReadTimeout(5 * TimeManager.ONE_MINUTE_IN_MILLIS.toInt())
//        setMockResponseDelay(0)
//        if (shouldMockEndpoints()) {
//            EnvironmentUtilities.setShouldMockUrls(true)
//        } else {
//            EnvironmentUtilities.setShouldMockUrls(false)
//        }
    }

    protected open fun prepareBeforeActivityLaunchedLastThing() {
        //By default, do nothing first thing before the activity is launched
    }

    protected open fun prepareBeforeActivityLaunchedFirstThing() {
        //By default, do nothing last thing before the activity is launched
    }

    protected open fun cleanUpAfterActivityFinishedLastThing() {
        //By default, do nothing last thing after the activity is finished
    }

    protected abstract fun injectComponentsForInstrumentationTest()

    //Convenience methods:
    protected fun getActivity() : Activity? {
        return getCurrentActivity()
    }
    protected fun getCurrentActivity() : Activity? {
        return TestUtilities.currentActivity;
    }
    protected fun screenshot(tag: String) {
        SpoonScreenshotUtilities.screenshot(tag)
    }
/*
    protected fun setMockResponseDelay(delayInMillis: Long) {
        Timber.d(".setMockResponseDelay(%d)", delayInMillis)
        MockWebDispatcher.sResponseDelay = delayInMillis
    }
*/
}
