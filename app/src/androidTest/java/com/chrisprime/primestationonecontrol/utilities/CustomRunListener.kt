package com.chrisprime.primestationonecontrol.utilities

import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import timber.log.Timber
import java.util.*

class CustomRunListener : RunListener() {

    @Throws(Exception::class)
    override fun testStarted(description: Description?) {
        Timber.d("testStarted() called with: description = %s", description)
        super.testStarted(description)
    }

    @Throws(Exception::class)
    override fun testFinished(description: Description?) {
        Timber.d(".testFinished(%s), capturing screenshot...", description)
        super.testFinished(description)
    }

    @Throws(Exception::class)
    override fun testFailure(failure: Failure?) {
        Timber.d(".testFailed(%s), capturing screenshot...", failure!!.message)
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored") val exceptionClassName = failure.exception.javaClass.simpleName
        val className = failure.description.className
        val methodName = failure.description.methodName
        SpoonScreenshotUtilities.screenshot(String.format(Locale.getDefault(), "%s%s%sFailureScreen", exceptionClassName, className, methodName), className, methodName)
        super.testFailure(failure)
    }
}
