package com.chrisprime.primestationonecontrol.utilities;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Locale;

import timber.log.Timber;

public class CustomRunListener extends RunListener {

    @Override
    public void testFinished(Description description) throws Exception {
        Timber.d(".testFinished(%s), capturing screenshot...", description);
        SpoonScreenshotUtilities.screenshot("FinishedScreen", description.getClassName(), description.getMethodName());
        super.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        Timber.d(".testFailed(%s), capturing screenshot...", failure.getMessage());
        SpoonScreenshotUtilities.screenshot(String.format(Locale.getDefault(), "%sFailureScreen",
                failure.getException().getClass().getSimpleName()), failure.getDescription().getClassName(), failure.getDescription().getMethodName());
        super.testFailure(failure);
    }
}
