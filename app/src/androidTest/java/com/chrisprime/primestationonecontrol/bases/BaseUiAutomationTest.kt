package com.chrisprime.primestationonecontrol.bases

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity
import com.chrisprime.primestationonecontrol.utilities.CustomFailureHandler

import com.chrisprime.primestationonecontrol.utilities.SpoonScreenshotUtilities

import org.junit.Before

import timber.log.Timber

abstract class BaseUiAutomationTest : BaseNoUiAutomationTest() {
    @Before
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        injectInstrumentation(InstrumentationRegistry.getInstrumentation())
        mActivity = super.getActivity()
        //enforceLocalMockedContent();
        //        waitForIdleSync();      //Left in because this might be needed for some fragile tests in the future
        setActivity(currentActivity)
        Espresso.setFailureHandler(CustomFailureHandler((mActivity as PrimeStationOneControlActivity?)!!))
    }

    override fun setActivity(testActivity: Activity) {
        Timber.d(".setActivity(%s)", testActivity)
        SpoonScreenshotUtilities.sScreenshotActivity = testActivity
        super.setActivity(testActivity)
    }

    protected fun screenshot(tag: String) {
        //        Activity currentActivity = getCurrentActivity();
        //        SpoonScreenshotUtilities.screenshot(tag, currentActivity);
        SpoonScreenshotUtilities.screenshot(tag)
    }
}
