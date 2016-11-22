package com.chrisprime.primestationonecontrol.bases;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;

import com.chrisprime.primestationonecontrol.utilities.CustomFailureHandler;
import com.chrisprime.primestationonecontrol.utilities.SpoonScreenshotUtilities;

import org.junit.Before;

import timber.log.Timber;

abstract public class BaseUiAutomationTest extends BaseNoUiAutomationTest {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        setMActivity(super.getActivity());
        //enforceLocalMockedContent();
//        waitForIdleSync();      //Left in because this might be needed for some fragile tests in the future
        setActivity(getCurrentActivity());
        Espresso.setFailureHandler(new CustomFailureHandler(getMActivity()));
    }

    @Override
    protected void setActivity(Activity testActivity) {
        Timber.d(".setActivity(%s)", testActivity);
        SpoonScreenshotUtilities.sScreenshotActivity = testActivity;
        super.setActivity(testActivity);
    }

    protected void screenshot(String tag) {
//        Activity currentActivity = getCurrentActivity();
//        SpoonScreenshotUtilities.screenshot(tag, currentActivity);
        SpoonScreenshotUtilities.screenshot(tag);
    }
}
